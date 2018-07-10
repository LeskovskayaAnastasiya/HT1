<?xml version="1.0" encoding="UTF-8" ?>
<%@ page import="app.Phonebook"%>
<%@ page import="app.Person"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Список людей</title>
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

        A.addNote:link { text-decoration: underline; }
        A.addNote:visited { text-decoration: none; }
        A.addNote:active { text-decoration: none; }
        A.addNote:hover {
            text-decoration: underline ;
            font-size: 24px; /* Размер шрифта */
            font-weight: bold; /* Жирное начертание */
            color: darkblue; /* Цвет ссылки */
        }


    </style>
</head>
<body bgcolor="#FFF5EE">

<%
    String user_message = "";
    HashMap<String,String> jsp_parameters = new HashMap<>();
    Phonebook phonebook = (Phonebook)request.getAttribute("phonebook");

    if (request.getAttribute("jsp_parameters") != null)
    {
        jsp_parameters = (HashMap<String,String>)request.getAttribute("jsp_parameters");
    }
    user_message = jsp_parameters.get("current_action_result_label");
%>
<table align="center" border="1" width="90%">

    <%
        if ((user_message != null)&&(!user_message.equals("")))
        {
    %>
    <tr>
        <td id="styleMassage" colspan="6" align="center" ><%=user_message%></td>
    </tr>
    <%
        }
    %>
    <tr>
        <td  colspan="6" align="center" ><a class = "addNote" href="<%=request.getContextPath()%>/?action=add">Добавить запись</a></td>
    </tr>
    <tr>
        <td align="center"><b>Фамилия</b></td>
        <td align="center"><b>Имя</b></td>
        <td align="center"><b>Отчество</b></td>
        <td align="center"><b>Телефон(ы)</b></td>
        <td align="center">&nbsp;</td>
        <td align="center">&nbsp;</td>
    </tr>

    <%
        for (Person person : phonebook.getContents().values()) {

    %>
    <tr>
        <td><%=person.getSurname()%></td>
        <td><%=person.getName()%></td>
        <td><%=person.getMiddlename()%></td>
        <td>
            <%
                for(String phone : person.getPhones().values())
                {
            %>
            <%=phone%><br />
            <%
                }
            %>
        </td>
        <td><a href="<%=request.getContextPath()%>/?action=edit&id=<%=person.getId()%>">Редактировать</a></td>
        <td><a href="<%=request.getContextPath()%>/?action=delete&id=<%=person.getId()%>">Удалить</a></td>
    </tr>
    <%
        }
    %>
</table>
</body>
</html>