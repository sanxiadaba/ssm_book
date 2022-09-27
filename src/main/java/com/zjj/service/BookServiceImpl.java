package com.zjj.service;

import com.zjj.pojo.Books;
import com.zjj.dao.BookMapper;
import com.zjj.sqlConfig.MySQLFileInit;

import java.util.List;
//@Service
public class BookServiceImpl implements BookService {

    // 初始化数据库
    static {
        MySQLFileInit.run();
    }

    //service调dao层，组合Dao
    //@Autowired
    private BookMapper bookMapper;

    public void setBookMapper(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    public void addBook(Books books) {
        bookMapper.addBook(books);
    }

    public void deleteBookById(int id) {
        bookMapper.deleteBookById(id);
    }

    public void updateBook(Books books) {
        bookMapper.updateBook(books);
    }

    public Books queryBookById(int id) {
        return bookMapper.queryBookById(id);
    }

    public List<Books> queryAllBooks() {
        return bookMapper.queryAllBooks();
    }

    public List<Books> queryBook(String bookName) {
        return bookMapper.queryBook(bookName);
    }
    public Books queryBookByName(String bookName) {
        return bookMapper.queryBookByName(bookName);
    }
}
