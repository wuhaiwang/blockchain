package com.seasun.management;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 以下为常用的数组操作,stream相关的api测试
 */
public class StreamTest {

    // simple object for test
    static class Book {

        private Integer rank;
        private String description;
        private String tag;

        public Book(Integer rank, String description) {
            super();
            this.rank = rank;
            this.description = description;
        }

        public Book(Integer rank, String description, String tag) {
            super();
            this.rank = rank;
            this.description = description;
            this.tag = tag;
        }

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }

    /**
     * 将数组转换为指定的map
     */
    @Test
    public void convertListToMap() {

        List<Book> books = new ArrayList();
        books.add(new Book(1, "西游记"));
        books.add(new Book(2, "红楼梦"));

        // Map<key, object>
        Map<Integer, Book> mappedBook = books.stream().collect(
                Collectors.toMap(b -> b.getRank(), (b) -> b));
        System.out.println((mappedBook.size()));

        // Map<key, someValue>
        Map<Integer, String> mappedDesc = books.stream().collect(
                Collectors.toMap(b -> b.getRank(), (b) -> b.getDescription()));

        System.out.println((mappedDesc.size()));
    }

    /**
     * 将指定的字段提取出来
     */
    @Test
    public void filterFieldToList() {

        List<Book> books = new ArrayList();
        books.add(new Book(1, "西游记"));
        books.add(new Book(2, "红楼梦"));

        List<String> des = books.stream().map(p->p.getDescription()).collect(Collectors.toList());

        System.out.println((des.get(0)));
    }



    /**
     * 按条件过滤数组
     */
    @Test
    public void filterList() {

        List<Book> books = new ArrayList();
        books.add(new Book(1, "西游记"));
        books.add(new Book(2, "红楼梦"));

        // key, object
        List<Book> hl = books.stream().filter(b -> b.description.equals("红楼梦")).collect(Collectors.toList());
        System.out.println((hl.size()));
    }

    /**
     * 删除数组元素
     */
    @Test
    public void removeItemFromList() {

        List<Book> books = new ArrayList();
        books.add(new Book(1, "西游记"));
        books.add(new Book(2, "红楼梦"));

        // key, object
        books.removeIf(b -> b.getDescription().equals("西游记"));
        System.out.println((books.size()));
    }

    /**
     * 数组分组,输出map
     */
    @Test
    public void groupFromList() {
        List<Book> books = new ArrayList();
        books.add(new Book(1, "西游记", "古典"));
        books.add(new Book(2, "红楼梦", "古典"));
        books.add(new Book(3, "浪漫满屋", "现代"));
        books.add(new Book(4, "人民的民义", "现代"));
        Map<String, List<Book>> groupedResult = books.stream().collect(Collectors.groupingBy(Book::getTag));
        System.out.println((groupedResult.size()));
    }


    /**
     * 判断数组是否包含某个值
     */
    @Test
    public void matchFromList() {
        List<Book> books = new ArrayList();
        books.add(new Book(1, "西游记", "古典"));
        books.add(new Book(2, "红楼梦", "古典"));
        books.add(new Book(3, "浪漫满屋", "现代"));
        boolean isMatched = books.stream().anyMatch(b -> b.getDescription().equals("浪漫满屋"));
        System.out.println((isMatched));

        boolean isMatchedAgain = books.stream().anyMatch(b -> b.getDescription().equals("浪漫满屋1"));
        System.out.println((isMatchedAgain));
    }

    @Test
    public void testCollectorEmpty() {
        List<Book> books = new ArrayList();
        books.add(new Book(1, "西游记", "古典"));
        books.add(new Book(2, "红楼梦", "古典"));
        books.add(new Book(3, "浪漫满屋", "现代"));
        List<Book> result = books.stream().filter(b -> b.getTag().equals("")).collect(Collectors.toList());
        if (result.size() > 0) {
            // will never enter this branch
            System.out.println(result.get(0));
        }
    }


}
