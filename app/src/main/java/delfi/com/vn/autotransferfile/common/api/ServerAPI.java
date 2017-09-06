package delfi.com.vn.autotransferfile.common.api;
import java.util.HashMap;

import delfi.com.vn.autotransferfile.common.api.request.GroupRequest;
import delfi.com.vn.autotransferfile.common.api.request.UserRequestOption;
import delfi.com.vn.autotransferfile.common.api.response.FolderResponse;
import delfi.com.vn.autotransferfile.common.api.response.GroupResponse;
import delfi.com.vn.autotransferfile.common.api.response.UserResponse;
import delfi.com.vn.autotransferfile.common.api.response.UserResponseOption;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

public interface ServerAPI {



    String USER_REGISTER = "/task_manager/v1/register";
    String USER_LOGIN = "/task_manager/v1/login";
    String TASKS_ADD = "/task_manager/v1/tasks";
    String TASKS_GET_LIST = "/task_manager/v1/tasks";
    String TASKS_UPDATE = "/task_manager/v1/tasks/{id}";
    String TASKS_DELETE = "/task_manager/v1/tasks/{id}";
    String TASKS_DETAIL = "/task_manager/v1/tasks/{id}";
    String TASKS_LOAD_MORE = "/task_manager/v1/tasksLoadMore";
    String USER_SIGNUP = "/api/user/Register";
    String GROUP_LOGIN = "/api/device/Login";
    String AUTO_GET_LIST_FOLDER = "/api/folder/GetCurentFolder";


    @FormUrlEncoded
    @POST(USER_REGISTER)
    Observable<UserResponse> userRegister(@FieldMap HashMap<String, Object> hashMap);

    @FormUrlEncoded
    @POST(USER_LOGIN)
    Observable<UserResponse> userLogin(@FieldMap HashMap<String, Object> hashMap);

    @POST(USER_SIGNUP)
    Observable<UserResponseOption> signUp(@Body UserRequestOption userRequest);

    @POST(GROUP_LOGIN)
    Observable<GroupResponse> login(@Body GroupRequest groupRequest);

    @GET(AUTO_GET_LIST_FOLDER)
    Observable<FolderResponse>getListFolder();


    @Streaming
    @GET
    Observable<Response<ResponseBody>> downloadFileByUrlRx(@Url String fileUrl);

    /*
    @FormUrlEncoded
    @POST(TASKS_ADD)
    Observable<TasksResponse> tasksAdding(@FieldMap HashMap<String, Object> hashMap);

    @GET(TASKS_GET_LIST)
    Observable<TasksResponse> tasksGetList();

    @FormUrlEncoded
    @POST(TASKS_LOAD_MORE)
    Observable<TasksResponse> taskLoadMore(@Field("current_item") String current_item);

    @FormUrlEncoded
    @PUT(TASKS_UPDATE)
    Observable<TasksResponse> tasksUpdating(@Path("id") String id);

    @FormUrlEncoded
    @DELETE(TASKS_DELETE)
    Observable<TasksResponse> tasksDeleting(@Path("id") String id);

    //Some examples for type of Body or Query

    @GET(TASKS_DETAIL)
    Observable<TasksResponse> getFrequentUser(@Query("page") int currentPage);

    @POST(TASKS_ADD)
    Observable<TasksResponse> inviteFriends(@Body UserRequest userRequest);

    */




}
