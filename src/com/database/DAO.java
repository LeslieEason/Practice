package com.database;

import java.sql.SQLException;
import java.util.List;

public interface DAO
{
    public void add(Human h) throws SQLException;
    public void update(Human h) throws SQLException;
    public void delete(Human h) throws SQLException;
    public Human get(int id) throws SQLException;
    public List<Human> list() throws SQLException;
}
