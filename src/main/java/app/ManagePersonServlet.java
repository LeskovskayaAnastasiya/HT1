package app;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ManagePersonServlet extends HttpServlet {

    // Идентификатор для сериализации/десериализации.
    private static final long serialVersionUID = 1L;

    // Основной объект, хранящий данные телефонной книги.
    private Phonebook phonebook;

    public ManagePersonServlet() {
        // Вызов родительского конструктора.
        super();

        // Создание экземпляра телефонной книги.
        try {
            this.phonebook = Phonebook.getInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Валидация ФИО и генерация сообщения об ошибке в случае невалидных данных.
    private String validatePersonFMLName(Person person) {
        String error_message = "";

        if (!person.validateFMLNamePart(person.getName(), false)) {

            error_message += "Имя должно быть строкой от 1 до 150 символов, может состоять из букв, цифр, знака подчёркивания и знака минус.<br />";
        }

        if (!person.validateFMLNamePart(person.getSurname(), false)) {
            error_message += "Фамилия должна быть строкой от 1 до 150 символов, может состоять из букв, цифр, знака подчёркивания и знака минус.<br />";
        }

        if (!person.validateFMLNamePart(person.getMiddlename(), true)) {
            error_message += "Отчество должно быть строкой от 0 до 150 символов, может состоять из букв, цифр, знака подчёркивания и знака минус.<br />";
        }

        return error_message;
    }

    private String validatePhone(String number) {

        String er_message = "";
        //boolean matherFlag = false;
        Pattern p = Pattern.compile("[0-9|+#-]{2,50}", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = p.matcher(number);

        //matherFlag = matcher.matches();
        if (!matcher.matches()) {
            er_message += "Требования к телефонному номеру: от 2 до 50 символов: цифра, +, -, #.";
        }
        return er_message;
    }

    // Реакция на GET-запросы.
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Обязательно ДО обращения к любому параметру нужно переключиться в UTF-8,
        // иначе русский язык при передаче GET/POST-параметрами превращается в "кракозябры".
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // В JSP нам понадобится сама телефонная книга. Можно создать её экземпляр там,
        // но с архитектурной точки зрения логичнее создать его в сервлете и передать в JSP.
        request.setAttribute("phonebook", this.phonebook);

        // Хранилище параметров для передачи в JSP.
        HashMap<String, String> jsp_parameters = new HashMap<String, String>();
        jsp_parameters.put("behaviour", "");


        // Диспетчеры для передачи управления на разные JSP (разные представления (view)).
        RequestDispatcher dispatcher_for_manager = request.getRequestDispatcher("/ManagePerson.jsp");
        RequestDispatcher dispatcher_for_list = request.getRequestDispatcher("/List.jsp");
        RequestDispatcher dispatcher_for_managePhone = request.getRequestDispatcher("/ManagePhonebook.jsp");

        // Действие (action) и идентификатор записи (id) над которой выполняется это действие.
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        String idPhone = request.getParameter("idPhone");

        // Если идентификатор и действие не указаны, мы находимся в состоянии
        // "просто показать список и больше ничего не делать".
        if ((action == null) && (id == null)) {
            request.setAttribute("jsp_parameters", jsp_parameters);
            dispatcher_for_list.forward(request, response);
        }
        // Если же действие указано, то...
        else {
            switch (action) {
                // Добавление записи.
                case "add":
                    // Создание поведения для отображения ManagePerson.jsp
                    jsp_parameters.put("behaviour", "addPerson");
                    // Создание новой пустой записи о пользователе.
                    Person empty_person = new Person();

                    // Подготовка параметров для JSP.
                    jsp_parameters.put("current_action", "add");
                    jsp_parameters.put("next_action", "add_go");
                    jsp_parameters.put("next_action_label", "Добавить");

                    // Установка параметров JSP.
                    request.setAttribute("person", empty_person);
                    request.setAttribute("jsp_parameters", jsp_parameters);

                    // Передача запроса в JSP.
                    dispatcher_for_manager.forward(request, response);
                    break;

                // Редактирование записи.
                case "edit":
                    // Извлечение из телефонной книги информации о редактируемой записи.
                    Person editable_person = this.phonebook.getPerson(id);

                    // Подготовка параметров для JSP.
                    jsp_parameters.put("current_action", "edit");
                    jsp_parameters.put("next_action", "edit_go");
                    jsp_parameters.put("next_action_label", "Сохранить");

                    // Установка параметров JSP.
                    request.setAttribute("person", editable_person);
                    request.setAttribute("jsp_parameters", jsp_parameters);

                    // Передача запроса в JSP.
                    dispatcher_for_manager.forward(request, response);
                    break;

                // Удаление записи.
                case "delete":

                    // Если запись удалось удалить...
                    if (phonebook.deletePerson(id)) {
                        jsp_parameters.put("current_action_result", "DELETION_SUCCESS");
                        jsp_parameters.put("current_action_result_label", "Удаление выполнено успешно");
                    }
                    // Если запись не удалось удалить (например, такой записи нет)...
                    else {
                        jsp_parameters.put("current_action_result", "DELETION_FAILURE");
                        jsp_parameters.put("current_action_result_label", "Ошибка удаления (возможно, запись не найдена)");
                    }

                    // Установка параметров JSP.
                    request.setAttribute("jsp_parameters", jsp_parameters);

                    // Передача запроса в JSP.
                    dispatcher_for_list.forward(request, response);
                    break;

                case "addPhoneToDb":

                    // Подготовка параметров для JSP.
                    jsp_parameters.put("current_action", "addPhoneToDb");
                    jsp_parameters.put("next_action", "addPhone_go");
                    jsp_parameters.put("next_action_label", "Добавить номер");
                    jsp_parameters.put("behaviour", "addPhone");
                    // Установка параметров JSP.
                    request.setAttribute("person", phonebook.getPerson(id));
                    request.setAttribute("jsp_parameters", jsp_parameters);

                    // Передача запроса в JSP.
                    dispatcher_for_managePhone.forward(request, response);
                    break;

                case "editPhone":

                    // Подготовка параметров для JSP.
                    jsp_parameters.put("current_action", "editPhone");
                    jsp_parameters.put("next_action", "editPhone_go");
                    jsp_parameters.put("next_action_label", "Сохранить номер");
                    jsp_parameters.put("behaviour", "editPhone");
                    //jsp_parameters.put("idPhone", "");
                    // Установка параметров JSP.
                    request.setAttribute("person", phonebook.getPerson(id));
                    request.setAttribute("numberFromDb",  phonebook.getPhoneFromDb(idPhone));
                    request.setAttribute("idPhone", idPhone);
                    request.setAttribute("jsp_parameters", jsp_parameters);

                    // Передача запроса в JSP.
                    dispatcher_for_managePhone.forward(request, response);
                    break;

                case "deletePhone":

                    Person person = this.phonebook.getPerson(request.getParameter("id"));

                    if (phonebook.deletePhone(idPhone)) {
                        person.deletePhone(idPhone);

                        request.setAttribute("person", person);
                        jsp_parameters.put("next_action", "edit_go");
                        jsp_parameters.put("current_action_result", "DELETION_SUCCESS");
                        jsp_parameters.put("current_action_result_label", "Удаление выполнено успешно");
                        jsp_parameters.put("next_action_label", "Сохранить");
                        // request.setAttribute("jsp_parameters", jsp_parameters);
                    }
                    // Если запись не удалось удалить (например, такой записи нет)...
                    else {
                        jsp_parameters.put("current_action_result", "DELETION_FAILURE");
                        jsp_parameters.put("current_action_result_label", "Ошибка удаления (возможно, запись не найдена)");
                    }
                    // Установка параметров JSP.
                    request.setAttribute("jsp_parameters", jsp_parameters);

                    // Передача запроса в JSP.
                    dispatcher_for_manager.forward(request, response);
                    break;

                // Возврат к списку
                case "returnToList":
                    // Передача запроса в JSP.
                    dispatcher_for_list.forward(request, response);
                    break;

                case "returnToManagePerson":

                    jsp_parameters.put("behaviour", "addPerson");
                    // Передача запроса в JSP.
                    dispatcher_for_manager.forward(request, response);
                    break;
            }
        }
    }

    // Реакция на POST-запросы.
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Обязательно ДО обращения к любому параметру нужно переключиться в UTF-8,
        // иначе русский язык при передаче GET/POST-параметрами превращается в "кракозябры".
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // В JSP нам понадобится сама телефонная книга. Можно создать её экземпляр там,
        // но с архитектурной точки зрения логичнее создать его в сервлете и передать в JSP.
        request.setAttribute("phonebook", this.phonebook);

        // Хранилище параметров для передачи в JSP.
        HashMap<String, String> jsp_parameters = new HashMap<String, String>();
        jsp_parameters.put("behaviour", "");
        // Диспетчеры для передачи управления на разные JSP (разные представления (view)).

        RequestDispatcher dispatcher_for_manager = request.getRequestDispatcher("/ManagePerson.jsp");
        RequestDispatcher dispatcher_for_list = request.getRequestDispatcher("/List.jsp");
        RequestDispatcher dispatcher_for_managePhone = request.getRequestDispatcher("/ManagePhonebook.jsp");


        // Действие (add_go, edit_go) и идентификатор записи (id) над которой выполняется это действие.
        String add_go = request.getParameter("add_go");
        String edit_go = request.getParameter("edit_go");
        String addPhone_go = request.getParameter("addPhone_go");
        String editPhone_go = request.getParameter("editPhone_go");
        String id = request.getParameter("id");
        String idPhone = request.getParameter("idPhone");
        Person updatable_person = this.phonebook.getPerson(id);
        String error_message;
        // Добавление записи.
        if (add_go != null) {
            // Создание записи на основе данных из формы.
            Person new_person = new Person(request.getParameter("name"), request.getParameter("surname"), request.getParameter("middlename"));

            // Валидация ФИО.
            error_message = this.validatePersonFMLName(new_person);

            // Если данные верные, можно производить добавление.
            if (error_message.equals("")) {

                // Если запись удалось добавить...
                if (this.phonebook.addPerson(new_person)) {
                    jsp_parameters.put("current_action_result", "ADDITION_SUCCESS");
                    jsp_parameters.put("current_action_result_label", "Добавление выполнено успешно");
                }
                // Если запись НЕ удалось добавить...
                else {
                    jsp_parameters.put("current_action_result", "ADDITION_FAILURE");
                    jsp_parameters.put("current_action_result_label", "Ошибка добавления");
                }

                // Установка параметров JSP.
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_list.forward(request, response);
            }
            // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
            else {
                // Подготовка параметров для JSP.
                jsp_parameters.put("current_action", "add");
                jsp_parameters.put("next_action", "add_go");
                jsp_parameters.put("next_action_label", "Добавить");
                jsp_parameters.put("error_message", error_message);
                jsp_parameters.put("behaviour", "addPerson");
                // Установка параметров JSP.
                request.setAttribute("person", new_person);
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_manager.forward(request, response);
            }
        }

        // Редактирование записи.
        if (edit_go != null) {
            Person checkPerson = new Person(request.getParameter("name"),request.getParameter("surname"),request.getParameter("middlename"));
            // Валидация ФИО.
            error_message = this.validatePersonFMLName(checkPerson);

            // Если данные верные, можно производить добавление.
            if (error_message.equals("")) {

                updatable_person.setName(request.getParameter("name"));
                updatable_person.setSurname(request.getParameter("surname"));
                updatable_person.setMiddlename(request.getParameter("middlename"));

                // Если запись удалось обновить...
                if (this.phonebook.updatePerson(id, updatable_person)) {
                    jsp_parameters.put("current_action_result", "UPDATE_SUCCESS");
                    jsp_parameters.put("current_action_result_label", "Обновление выполнено успешно");
                }
                // Если запись НЕ удалось обновить...
                else {
                    jsp_parameters.put("current_action_result", "UPDATE_FAILURE");
                    jsp_parameters.put("current_action_result_label", "Ошибка обновления");
                }

                // Установка параметров JSP.
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_list.forward(request, response);
            }
            // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
            else {
                // Подготовка параметров для JSP.
                jsp_parameters.put("current_action", "edit");
                jsp_parameters.put("next_action", "edit_go");
                jsp_parameters.put("next_action_label", "Сохранить");
                jsp_parameters.put("error_message", error_message);

                // Установка параметров JSP.

                request.setAttribute("person", checkPerson);
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_manager.forward(request, response);
            }
        }
        // Редактирование номера телефона
        if (addPhone_go != null) {

            String number = request.getParameter("phoneNumber").trim();
            error_message = this.validatePhone(number);

            if (error_message.equals("")) {
                phonebook.addPhoneToDb(number, updatable_person.getId());
                updatable_person.getDataPhone();

                // Подготовка параметров для JSP.
                jsp_parameters.put("next_action", "edit_go");
                jsp_parameters.put("next_action_label", "Сохранить");
                jsp_parameters.put("current_action_result_label", "Добавление телефона выполнено успешно");
                request.setAttribute("person", updatable_person);
                request.setAttribute("jsp_parameters", jsp_parameters);
                dispatcher_for_manager.forward(request, response);
            } else {
                // Подготовка параметров для JSP.
                jsp_parameters.put("current_action", "addPhoneToDb");
                jsp_parameters.put("next_action", "addPhone_go");
                jsp_parameters.put("next_action_label", "Сохранить");
                jsp_parameters.put("error_message", error_message);
                jsp_parameters.put("behaviour", "addPerson");

                // Установка параметров JSP.
                request.setAttribute("person", updatable_person);
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_managePhone.forward(request, response);
            }
        }
        if (editPhone_go != null) {

            String number = request.getParameter("phoneNumber").trim();
            error_message = this.validatePhone(number);

            if (error_message.equals("")) {
                phonebook.updatePhoneFromDb(idPhone,updatable_person.getId(), number);
                updatable_person.getDataPhone();
                // Подготовка параметров для JSP.
                jsp_parameters.put("next_action", "edit_go");
                jsp_parameters.put("next_action_label", "Добавить номер");
                jsp_parameters.put("current_action_result_label", "Редактирование телефона выполнено успешно");
                request.setAttribute("person", updatable_person);
                request.setAttribute("jsp_parameters", jsp_parameters);
                dispatcher_for_manager.forward(request, response);
            } else {

                //   Подготовка параметров для JSP.
                jsp_parameters.put("current_action", "editPhone_go");
                jsp_parameters.put("next_action", "addPhone_go");
                jsp_parameters.put("next_action_label", "Сохранить");
                jsp_parameters.put("error_message", error_message);
                jsp_parameters.put("behaviour", "addPerson");

                // Установка параметров JSP.
                request.setAttribute("person", updatable_person);
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_managePhone.forward(request, response);
            }
        }
    }
}


