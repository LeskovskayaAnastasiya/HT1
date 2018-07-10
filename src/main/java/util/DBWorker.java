package util;

import java.sql.*;

public class DBWorker {

    // Количество рядов таблицы, затронутых последним запросом.
    private Integer affected_rows = 0;

    // Значение автоинкрементируемого первичного ключа, полученное после
    // добавления новой записи.
    private Integer last_insert_id = 0;

    // Указатель на экземпляр класса.
    private static DBWorker instance = null;

    private  Statement statement;
    private  Connection connect;

    // Метод для получения экземпляра класса (реализован Singleton).
    public static DBWorker getInstance()
    {
        if (instance == null)
        {
            instance = new DBWorker();
        }

        return instance;
    }

    // "Заглушка", чтобы экземпляр класса нельзя было получить напрямую.
    private DBWorker()
    {
        // Просто "заглушка".
    }

    // Выполнение запросов на выборку данных.
    public ResultSet getDBData(String query)
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/phonebook?user=root&password=ROOT&useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8&connectionCollation=utf8_general_ci");

            //connect = DriverManager.getConnection("jdbc:mysql://localhost/phonebook?user=root&password=123456&useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8&connectionCollation=utf8_general_ci");
            statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet;
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }

        System.out.println("null on getDBData()!");
        return null;

    }

    // Выполнение запросов на модификацию данных.
    public Integer changeDBData(String query)
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/phonebook?user=root&password=ROOT&useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8&connectionCollation=utf8_general_ci");

            statement = connect.createStatement();
            this.affected_rows = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

            // Получаем last_insert_id() для операции вставки.
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()){
                this.last_insert_id = rs.getInt(1);
            }

            return this.affected_rows;
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }

        System.out.println("null on changeDBData()!");
        return null;
    }

    public void updataPhoneNumber(String query) {

        //System.out.println(query);
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/phonebook?user=root&password=ROOT&useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8&connectionCollation=utf8_general_ci");
            statement = connect.createStatement();
            statement.executeUpdate(query);
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++
    // Геттеры и сеттеры.
    public Integer getAffectedRowsCount()
    {
        return this.affected_rows;
    }

    public Integer getLastInsertId()
    {
        return this.last_insert_id;
    }
    // Геттеры и сеттеры.
    // -------------------------------------------------
}

