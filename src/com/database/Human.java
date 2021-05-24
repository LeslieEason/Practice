package com.database;
//对应test表的ORM对象
public class Human {
    public int id;
    public String name;
    public int number;

    public  Human(int id,String name,int number)
    {
        this.id=id;
        this.name=name;
        this.number=number;
    }

    public Human()
    {

    }

    @Override
    public String toString() {
        return "Human[" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", number=" + number +
                ']'+'\n';
    }
}
