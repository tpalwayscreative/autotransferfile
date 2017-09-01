package delfi.com.vn.autotransferfile.common.api.request;

import dk.delfi.core.common.api.request.BaseRequest;

public class UserRequest extends BaseRequest {

    public String email;
    public String password;
    public String name ;

    public UserRequest(String email,String password,String name) {
        this.email = email ;
        this.password = password;
        this.name = name;
    }
}
