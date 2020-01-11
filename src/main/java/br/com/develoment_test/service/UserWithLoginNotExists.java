package br.com.develoment_test.service;

public class UserWithLoginNotExists extends RuntimeException {

    public UserWithLoginNotExists() {
        super("Usuário com o login informado não existe.");
    }
}
