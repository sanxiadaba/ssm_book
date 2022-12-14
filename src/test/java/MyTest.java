import com.zjj.pojo.Books;
import com.zjj.service.BookService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class MyTest {
    @Test
    public void test() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");

        BookService bookService = (BookService)applicationContext.getBean("BookServiceImpl");

        List<Books> books = bookService.queryAllBooks();

        for (Books book : books) {
            System.out.println(book);
        }
    }
}
