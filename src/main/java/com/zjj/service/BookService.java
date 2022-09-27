package com.zjj.service;

import com.zjj.pojo.Books;

import java.util.List;

public interface BookService {
    // 增加一本书
    void addBook(Books books);

    //删除一本书
    void deleteBookById(int id);

    //更改一本书
    void updateBook(Books books);

    //查询一本书
    Books queryBookById(int id);

    //查询全部的书
    List<Books> queryAllBooks();

    List<Books> queryBook(String bookName);

    Books queryBookByName(String bookName);

}
