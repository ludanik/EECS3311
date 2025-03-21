package org.example;

class User {
    private String email;
    private String password;
    private UserType userType;
    private boolean pendingValidation;

    public User(String email, String password, UserType userType, boolean validation) {
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.pendingValidation = validation;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserType getUserType() {
        return userType;
    }

    public boolean isPendingValidation() {
        return pendingValidation;
    }

    public void setPendingValidation(boolean pendingValidation) {
        this.pendingValidation = pendingValidation;
    }
}