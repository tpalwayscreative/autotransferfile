package delfi.com.vn.autotransferfile.common.api.response;
import delfi.com.vn.autotransferfile.model.CUser;
import dk.delfi.core.common.api.response.BaseResponse;

/**
 * Created by PC on 9/6/2017.
 */

public class GroupResponse extends BaseResponse {

    public CUser user ;
    public GroupResponse(CUser user){
       this.user = user;
    }

}
