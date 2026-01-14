package ci553.happyshop.client.auth;

import ci553.happyshop.auth.User;

public class LoginController {
    public LoginModel loginModel;

    public User login(String username, String password) {
        return loginModel.authenticateUser(username, password);
    }
}