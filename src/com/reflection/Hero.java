package com.reflection;

public class Hero
{
    public String name;
    public float hp;
    public int damage;
    private int id;

    static String copyright;
    static
    {
        System.out.println("初始化 copyright");
        copyright = "版权由Riot Games公司所有";
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Hero()
    {
    }

    public Hero(String name,float hp,int damage,int id)
    {
        this.name=name;
        this.hp=hp;
        this.damage=damage;
        this.id=id;
    }

    public Hero(String string) {
        name =string;
    }

    public boolean isDead() {
        // TODO Auto-generated method stub
        return false;
    }
    public void attackHero(Hero h2)
    {
        System.out.println(this.name+ " 正在攻击 " + h2.getName());
    }

    @Override
    public String toString()
    {
        return "Hero{" +
                "name='" + name + '\'' +
                ", hp=" + hp +
                ", damage=" + damage +
                ", id=" + id +
                '}'+"\r\n";
    }
}
