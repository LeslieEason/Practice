package com.socket.robot;
//人机对话的ORM类
public class Dictionary
{
    int id;
    String receive;
    String response;
    public Dictionary(int id,String receive,String response)
    {
        this.id=id;
        this.receive=receive;
        this.response=response;
    }

    @Override
    public String toString() {
        return "Dictionary{" +
                "id=" + id +
                ", receive='" + receive + '\'' +
                ", response='" + response + '\'' +
                '}'+'\n';
    }
}
