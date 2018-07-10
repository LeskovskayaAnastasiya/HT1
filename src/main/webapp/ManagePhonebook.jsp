<?xml version="1.0" encoding="UTF-8" ?>
<%@ page import="app.Person"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Управление данными о человеке</title>
    <style type="text/css">
        TABLE {
            border-collapse: collapse; /* Убираем двойные границы между ячейками */
            background:#F0F8FF; /* Цвет фона таблицы */
            border: 4px solid #696969; /* Рамка вокруг таблицы */
        }
        TD, TH {
            padding: 5px; /* Поля вокруг текста */
            border: 2px solid #696969; /* Рамка вокруг ячеек */
        }
        #styleMassage{
            font-style: italic;
            font-family: Arial, Helvetica, Verdana, sans-serif; /* Гарнитура шрифта */
            font-size: 150%; /* Размер текста */
            font-weight: lighter; /* Светлое начертание */
            color: brown;
        }
        input.button {
            position: relative;
            display: inline-block;
            font-size: 100%;
            font-weight: 700;
            color: #fff;
            text-shadow: #053852 -1px 1px, #053852 1px 1px, #053852 1px -1px, #053852 -1px -1px;
            text-decoration: none;
            user-select: none;
            padding: .3em .7em;
            outline: none;
            border-radius: 7px;
            background: #F6CEF5 repeating-linear-gradient(135deg, #F6CEF5, #F6CEF5 10px, #1679ad 10px, #1679ad 20px, #053852 20px);
            box-shadow:
                    inset -2px -2px rgba(0,0,0,.3),
                    inset 2px 2px rgba(255,255,255,.3);
            transition: background-position 999999s, color 999999s, text-shadow 999999s;
        }
        input.button:hover, a.button12:focus {
            text-shadow: #81F7F3 -1px 1px, #81F7F3 1px 1px, #81F7F3 1px -1px, #0175b1 -1px -1px;
            background: #D8F781 repeating-linear-gradient(135deg, #D8F781, #D8F781 10px, #8fd2f5 10px, #8fd2f5 20px, #0175b1 20px) no-repeat;
            background-size: 1000% 100%;
        }
        input.button:hover {
            background-position: 0 0;
        }
        input.button:focus {
            color: rgba(255,255,255,0);
            text-shadow: rgba(1,117,177,0) -1px 1px, rgba(1,117,177,0) 1px 1px, rgba(1,117,177,0) 1px -1px, rgba(1,117,177,0) -1px -1px;
            background-position: 900% 0;
            transition: background-position linear 600s, color .5s, text-shadow .5s;
        }
        input.button:after {
            content: "загрузка\2026";
            position: absolute;
            top: 0;
            left: 0;
            padding: .3em .7em;
            color: rgba(0,0,0,0);
            text-shadow: none;
            transition: 999999s;
        }
        input.button:focus:after {
            color: #fff;
            text-shadow: #0175b1 -1px 1px, #0175b1 1px 1px, #0175b1 1px -1px, #0175b1 -1px -1px;
            transition: .5s;
        }
    </style>
</head>
<body>

<%
    HashMap<String,String> jsp_parameters = new HashMap<>();
    Person person = null;
    String error_message = "";
    String numberFromDb = null;
    String idPhone = null;

    if (request.getAttribute("jsp_parameters") != null)
    {
        jsp_parameters = (HashMap<String,String>)request.getAttribute("jsp_parameters");
    }

    if (request.getAttribute("person") != null)
    {
        person = (Person)request.getAttribute("person");
    }
    if (request.getAttribute("numberFromDb") != null)
    {
        numberFromDb = (String)request.getAttribute("numberFromDb");
        idPhone = (String)request.getAttribute("idPhone");
    }
    error_message = jsp_parameters.get("error_message");

%>

<form action="<%=request.getContextPath()%>/" method="post">
    <input type="hidden" name="idPhone" value="<%=idPhone%>"/>
    <input type="hidden" name="id" value="<%=person.getId()%>"/>
    <table align="center" border="1" width="70%">
        <%
            if ((error_message != null)&&(!error_message.equals("")))
            {
        %>
        <tr>
            <td id="styleMassage" colspan="2" align="center"><span style="color:red"><%=error_message%></span></td>
        </tr>
        <%
            }
        %>
        <tr>
            <td colspan="2" align="center"  >Информация о телефоне владельца: <%=person.getSurname()%> <%=person.getName()%> <%=person.getMiddlename()%></td>
        </tr>
        <tr>
            <td>Номер:</td>
            <%
                if (jsp_parameters.get("behaviour").equals( "editPhone"))
                {
            %>
            <td><input type="text" name="phoneNumber" value="<%=numberFromDb%>"/> </td>
            <%
            }else {
            %>
            <td><input type="text" name="phoneNumber" value="<%=person.getPhoneNumber()%>"/> </td>
            <%
                }
            %>
        </tr>
        <tr>
            <td colspan="2" align="center">
                <input class = "button" type="submit" name="<%=jsp_parameters.get("next_action")%>" value="<%=jsp_parameters.get("next_action_label")%>" /> </br>
                <a href="<%=request.getContextPath()%>/?action=edit&id=<%=person.getId()%>">Вернуться к данным о человеке</a>
            </td>
        </tr>
    </table>
</form>
</body>
</html>