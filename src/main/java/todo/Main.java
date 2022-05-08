package todo;

import java.time.LocalDate;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import guice.PersistenceModule;

import todo.model.Todo;

public class Main {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) 	{
        Injector injector = Guice.createInjector(new PersistenceModule("test"));
        TodoDao todoDao = injector.getInstance(TodoDao.class);

        Todo todo1 = Todo.builder()
                .description("Buy beer")
                .priority(Todo.Priority.NORMAL)
                .tags(Lists.newArrayList("shopping"))
                .build();
        logger.info("Todo 1: {}", todo1);

        Todo todo2 = Todo.builder()
                .description("Do homework")
                .priority(Todo.Priority.HIGH)
                .dueDate(LocalDate.now().plusWeeks(1))
                .tags(Lists.newArrayList("school", "programming"))
                .build();
        logger.info("Todo 2: {}", todo2);

        todoDao.persist(todo1);
        todoDao.persist(todo2);
        logger.info("Todo 1: {}", todo1);
        logger.info("Todo 2: {}", todo2);

        logger.info("Unfinished tasks: {}", todoDao.findUnfinished());
        logger.info("High priority tasks: {}", todoDao.findByPriority(Todo.Priority.HIGH));
        logger.info("All tasks: {}", todoDao.findAll());

        todoDao.remove(todo2);
        logger.info("All tasks: {}", todoDao.findAll());

        todo1.setDone(true);
        todoDao.update(todo1);
        logger.info("All tasks: {}", todoDao.findAll());
    }

}
