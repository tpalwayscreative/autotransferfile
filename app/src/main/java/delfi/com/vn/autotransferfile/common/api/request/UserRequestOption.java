package delfi.com.vn.autotransferfile.common.api.request;
import dk.delfi.core.common.api.request.BaseRequest;

/**
 * Created by PC on 9/1/2017.
 */

public class UserRequestOption extends BaseRequest {

    public String UserName ;
    public String Password;
    public String Email ;

    public UserRequestOption(String UserName,String Password,String Email){
        this.UserName = UserName;
        this.Password = Password;
        this.Email = Email;
    }


}
