package delfi.com.vn.autotransferfile.common.api.response;
import dk.delfi.core.common.api.response.BaseResponse;

/**
 * Created by PC on 9/6/2017.
 */

public class GroupResponse extends BaseResponse {

    public String access_token ;
    public String device_id ;
    public GroupResponse(String access_token,String device_id){
        this.access_token = access_token;
        this.device_id = device_id;
    }

}
