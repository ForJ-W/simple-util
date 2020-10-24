package com.simple.util.reflet;


import org.junit.Test;

import java.lang.reflect.Field;

/**
 * @author wujing
 * @date 2020/10/21 21:22
 */
public class ClassSuperTest extends ClassSuperTestSuper {

    private String s1;
    private String s2;
    private String s3;

    @Test
    public void test1() {

        ClassSuper<ClassSuperTest> classSuper = ClassSuper.getInstance(ClassSuperTest.class);
        classSuper.getExtentsReverseFields();
        classSuper.getExtentsReverseList();
        classSuper.getExtentsFields();
        classSuper.getExtentsList();
        classSuper.getExtentsList();
        classSuper.getExtentsFields();
        classSuper.getExtentsReverseList();
        classSuper.getExtentsReverseFields();
        for (Class<? super ClassSuperTest> cls : classSuper.getExtentsList()) {

            System.out.println(cls.getSimpleName());
        }
        for (Field extentsField : classSuper.getExtentsFields()) {

            System.out.println(extentsField.getName());
        }
        for (Class<? super ClassSuperTest> cls : classSuper.getExtentsReverseList()) {

            System.out.println(cls.getSimpleName());
        }
        for (Field extentsField : classSuper.getExtentsReverseFields()) {

            System.out.println(extentsField.getName());
        }
    }


}

class ClassSuperTestSuper {

    private String superS1;
    private String superS2;
    private String superS3;
}