package br.com.develoment_test.web.rest;


import br.com.develoment_test.domain.User;
import br.com.develoment_test.repository.UserRepository;
import br.com.develoment_test.security.AuthoritiesConstants;
import br.com.develoment_test.security.SecurityUtils;
import br.com.develoment_test.service.MailService;
import br.com.develoment_test.service.UserService;
import br.com.develoment_test.service.dto.PasswordChangeDTO;
import br.com.develoment_test.service.dto.UserDTO;
import br.com.develoment_test.web.rest.errors.EmailAlreadyUsedException;
import br.com.develoment_test.web.rest.errors.EmailNotFoundException;
import br.com.develoment_test.web.rest.errors.InvalidPasswordException;
import br.com.develoment_test.web.rest.errors.LoginAlreadyUsedException;
import br.com.develoment_test.web.rest.vm.KeyAndPasswordVM;
import br.com.develoment_test.web.rest.vm.ManagedUserVM;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

/**
 * GraphQL controller for managing the current user's account.
 */
@Component
public class AccountResource implements GraphQLQueryResolver, GraphQLMutationResolver {

    private static class AccountResourceException extends RuntimeException {
        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    public AccountResource(UserRepository userRepository, UserService userService, MailService mailService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
    }

    /**
     * {@code GraphQL registerAccount } : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PreAuthorize("@functionalityRepository." +
        "getByNameAndAuthority_Name(\"registerAccount\", \"" + AuthoritiesConstants.ANONYMOUS + "\") != null")
    @ResponseStatus(HttpStatus.CREATED)
    public User registerAccount(@Valid ManagedUserVM managedUserVM) {
        System.out.println();
        if (!checkPasswordLength(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        return userService.registerUser(managedUserVM, managedUserVM.getPassword());
//        mailService.sendActivationEmail(user);
    }

    /**
     * {@code GraphQL activateAccount } : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @PreAuthorize("@functionalityRepository." +
        "getByNameAndAuthority_Name(\"activateAccount\", \"" + AuthoritiesConstants.ANONYMOUS + "\") != null")
    public void activateAccount(String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this activation key");
        }
    }

    /**
     * {@code isAuthenticated } : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @PreAuthorize("@functionalityRepository." +
        "getByNameAndAuthority_Name(\"isAuthenticated\", \"" + AuthoritiesConstants.ANONYMOUS + "\") != null")
    public String isAuthenticated(HttpServletRequest request) {
        return request.getRemoteUser();
    }

    /**
     * {@code GraphQL } : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @PreAuthorize("@functionalityRepository." +
        "getByNameAndAuthority_Name(\"getAccount\", \"" + AuthoritiesConstants.USER + "\") != null")
    public UserDTO getAccount() {
        return userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

    /**
     * {@code saveAccount} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PreAuthorize("@functionalityRepository." +
        "getByNameAndAuthority_Name(\"saveAccount\", \"" + AuthoritiesConstants.USER + "\") != null")
    public Boolean saveAccount(@Valid UserDTO userDTO) {
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new AccountResourceException("Current user login not found"));
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            throw new AccountResourceException("User could not be found");
        }
        userService.updateUser(userDTO.getEmail(),
            userDTO.getLangKey());
        return true;
    }

    /**
     * {@code changePassword} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PreAuthorize("@functionalityRepository." +
        "getByNameAndAuthority_Name(\"changePassword\", \"" + AuthoritiesConstants.ANONYMOUS + "\") != null")
    public boolean changePassword(PasswordChangeDTO passwordChangeDto) {
        if (!checkPasswordLength(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
        return true;
    }

    /**
     * {@code requestPasswordReset} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     * @throws EmailNotFoundException {@code 400 (Bad Request)} if the email address is not registered.
     */
//    @PreAuthorize("@functionalityRepository." +
//        "getByNameAndAuthority_Name(\"requestPasswordReset\", \"" + AuthoritiesConstants.ADMIN + "\") != null")
    public String requestPasswordReset(String mail) {
       mailService.sendPasswordResetMail(
           userService.requestPasswordReset(mail)
               .orElseThrow(EmailNotFoundException::new)
       );
       return "Requisição de reset de senha realizada!";
    }

    /**
     * {@code finishPasswordReset} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PreAuthorize("@functionalityRepository." +
        "getByNameAndAuthority_Name(\"finishPasswordReset\", \"" + AuthoritiesConstants.ANONYMOUS + "\") != null")
    public String finishPasswordReset(KeyAndPasswordVM keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user =
            userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this reset key");
        }
        return "Senha resetada";
    }

    private static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
            password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
            password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
    }
}
