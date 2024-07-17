package net.azurewebsites.planner.api.Services;

import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class EmailValidatorService {
    public static Boolean isValid(String email) {
        String[] parts = email.split("@");

        if (parts.length != 2) {
            return false;
        }

        String domain = parts[1];

        try {
            InetAddress address = InetAddress.getByName(domain);
            return true;
        } catch (UnknownHostException error) {
            return false;
        }
    }
}
