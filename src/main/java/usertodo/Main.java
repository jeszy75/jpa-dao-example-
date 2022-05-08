package usertodo;

import java.time.LocalDate;
import java.util.Locale;

import com.github.javafaker.Faker;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import guice.PersistenceModule;
import user.model.User;
import user.UserDao;
import usertodo.model.UserTodo;

public class Main {

    private static Logger logger = LogManager.getLogger();

    private static Faker faker = new Faker(new Locale("en"));

    private static User createUser(String username) {
        return User.builder()
                .username(username)
                .password(faker.internet().password())
                .name(faker.name().name())
                .email(faker.internet().emailAddress())
                .build();
    }

    private static UserTodo createTodo(User user) {
        return UserTodo.builder()
                .user(user)
                .description(faker.lorem().sentence(3))
                .priority(faker.options().option(UserTodo.Priority.class))
                .done(faker.bool().bool())
                .dueDate(LocalDate.now().plusDays(faker.number().numberBetween(1, 10)))
                .build();
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new PersistenceModule("test"));
        UserDao userDao = injector.getInstance(UserDao.class);
        UserTodoDao userTodoDao = injector.getInstance(UserTodoDao.class);

        for (var i = 0; i < 5; i++) {
            User user = createUser("user" + i);
            userDao.persist(user); // Persisting the user
            logger.info("Persisted: {}", user);
            for (var j = 0; j < 5; j++) {
                UserTodo userTodo = createTodo(user);
                userTodoDao.persist(userTodo); // Persisting the task
                logger.info("Persisted: {}", userTodo);
            }
        }
        User user = userDao.findByUsername("user4").get();
        logger.info("All tasks of {}:", user.getUsername());
        userTodoDao.findAll(user).forEach(logger::info);

        logger.info("Unfinished tasks of {}:", user.getUsername());
        userTodoDao.findUnfinished(user).forEach(logger::info);

        logger.info("High priority tasks of {}:", user.getUsername());
        userTodoDao.findByPriority(user, UserTodo.Priority.HIGH).forEach(logger::info);

        UserTodo userTodo = userTodoDao.findAll(user).get(0);
        userTodoDao.remove(userTodo); // Removing the first task of the user

        userTodoDao.findAll(user).forEach(userTodoDao::remove); // Removing all remaining tasks of the user (required for being able to remove the user)
        userDao.remove(user); // Removing the user
    }

}
