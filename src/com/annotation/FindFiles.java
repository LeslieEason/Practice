package com.annotation;
//@repeatable的案例
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class FindFiles {
    @Target( METHOD)
    @Retention( RetentionPolicy.RUNTIME )
    public @interface FileTypes {
        FileType[] value();
    }

    @Target(  METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    @Repeatable( FileTypes.class )
    public @interface FileType {
        String value();
    };

    @FileType( ".java" )
    @FileType( ".html" )
    @FileType( ".css" )
    @FileType( ".js" )
    public void work(){

        try {
            FileType[] fileTypes= this.getClass().getMethod("work").getAnnotationsByType(FileType.class);
            System.out.println("将从如下后缀名的文件中查找文件内容");
            for (FileType fileType : fileTypes) {
                System.out.println(fileType.value());
            }
            System.out.println("查找过程略。。。");
        } catch (NoSuchMethodException | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new FindFiles().work();
    }
}
