package com.firealert.backend.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "users")
@Getter
@Setter
public class User {

    @Id
    private String id;

    private String name;
    private String email;
    @JsonIgnore
    private String password;

    private String deviceId;

    public User(){
    }

    public User(String name, String email, String password, String deviceId){
        this.name=name;
        this.email=email;
        this.password=password;
        this.deviceId=deviceId;
    }


/*    CODE REQUIRED WITH OUT Getter and Setter ANNOTATIONS
     OR WE CAN USE @Data---> automatically provides
                      getters,setters,toString(),equals(),hashCode(),required constructor
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }*/
}



