package delfi.com.vn.autotransferfile.common.api.response;

import java.util.List;

import delfi.com.vn.autotransferfile.model.CFileDocument;
import dk.delfi.core.common.api.response.BaseResponse;

/**
 * Created by PC on 9/6/2017.
 */

public class FileDocumentResponse extends BaseResponse {
    public List<CFileDocument> files;
}
