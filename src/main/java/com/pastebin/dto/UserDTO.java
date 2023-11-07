package com.pastebin.dto;

import com.pastebin.entity.Message;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class UserDTO {
    private String email;
    private char[] password;

    private Set<Message> allUserMessages;

    public UserDTO(String email, char[] password) {
        this.email = email;
        this.password = password;
    }

    public UserDTO(String email, Set<Message> allUserMessages) {
        this.email = email;
        this.allUserMessages = allUserMessages;
    }

    public UserDTO(Set<Message> allUserMessages) {
        this.allUserMessages = allUserMessages;
    }

    public UserDTO(String email) {
        this.email = email;
    }

    public UserDTO() {
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDTO userDTO)) return false;
        return Objects.equals(getEmail(), userDTO.getEmail()) && Arrays.equals(getPassword(), userDTO.getPassword());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getEmail());
        result = 31 * result + Arrays.hashCode(getPassword());

        return result;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public Set<Message> getAllUserMessages() {
        return allUserMessages;
    }

    public void setAllUserMessages(Set<Message> allUserMessages) {
        this.allUserMessages = allUserMessages;
    }
}
