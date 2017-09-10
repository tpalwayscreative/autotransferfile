package delfi.com.vn.autotransferfile.common.api.request;

import dk.delfi.core.common.api.request.BaseRequest;

/**
 * Created by PC on 9/6/2017.
 */

public class FileDocumentRequest extends BaseRequest {

    public int file_document_id ;

    public FileDocumentRequest(int file_document_id){
        this.file_document_id = file_document_id;
    }

}
