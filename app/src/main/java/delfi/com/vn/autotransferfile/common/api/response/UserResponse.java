package delfi.com.vn.autotransferfile.common.api.response;
import com.google.gson.Gson;
import delfi.com.vn.autotransferfile.model.CUser;
import dk.delfi.core.common.api.response.BaseResponse;

public class UserResponse extends BaseResponse {
    public String name;
    public String email;
    public String apiKey;
    public String createdAt;
    public CUser cUser(){
        Gson g = new Gson();
        return g.fromJson(toFormResponse(),CUser.class);
    }
}
