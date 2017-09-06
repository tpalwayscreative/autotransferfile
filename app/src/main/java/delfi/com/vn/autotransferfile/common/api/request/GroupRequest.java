package delfi.com.vn.autotransferfile.common.api.request;

import dk.delfi.core.common.api.request.BaseRequest;

/**
 * Created by PC on 9/6/2017.
 */

public class GroupRequest extends BaseRequest {

    public String group_name ;
    public String device_id ;
    public String device_name ;
    public GroupRequest(String group_name,String device_id,String device_name){
        this.group_name = group_name;
        this.device_id = device_id;
        this.device_name = device_name;
    }
}
