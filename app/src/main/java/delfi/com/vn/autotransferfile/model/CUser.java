package delfi.com.vn.autotransferfile.model;
import java.io.Serializable;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by PC on 8/3/2017.
 */

public class CUser extends RealmObject implements Serializable{

    @PrimaryKey
    public String apiKey;
    public String name;
    public String email;
    public String createdAt;

    public CUser(){

    }

    public CUser(String name, String email, String apiKey, String createdAt){
        this.name = name ;
        this.email = email ;
        this.apiKey = apiKey ;
        this.createdAt = createdAt;
    }


}
