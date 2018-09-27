package com.eshel.ap;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import annotation.Bind;

@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor{
    private Filer mFiler;
    private Messager mMessager;
    private Elements mElementUtils;
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        //用于生成源码文件
        mFiler = processingEnvironment.getFiler();
        //用于打印日志
        mMessager = processingEnvironment.getMessager();
        //工具类, 从 Element 对象中获取 包名类名字段名等
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        //添加 MyProcessor要处理的注解
        Set<String> anno = new HashSet<>();
        anno.add(Bind.class.getCanonicalName());
        return anno;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        //得到所有Bind注解元素(所有类中的@Bind)
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Bind.class);
        note("elementsSize: "+elements.size());
        HashMap<String, List<Element>> elementMap = new HashMap<>();
        for (Element element : elements) {
            //获取包名+类名
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            String enclosingName = enclosingElement.getQualifiedName().toString();
            note(String.format("enclosindClass = %s", enclosingName));
            //日志打印结果  注: enclosindClass = com.eshel.annotationprocessor.MainActivity

            //根据包名类名对所有注解进行分类
            List<Element> elementList = elementMap.get(enclosingName);
            if(elementList == null) {
                elementList = new ArrayList<>();
                elementMap.put(enclosingName, elementList);
            }
            elementList.add(element);
        }

        for (Map.Entry<String, List<Element>> entry : elementMap.entrySet()) {
            createFile(entry.getKey(), entry.getValue());
        }
        return true;
    }

    private void createFile(String key, List<Element> value) {
        if(value.size() == 0)
            return;
        try {
            JavaFileObject jfo = mFiler.createSourceFile(key + "_ViewBinding", new Element[]{});
            Writer writer = jfo.openWriter();

            //获取包名
            TypeElement enclosingElement = (TypeElement) value.get(0).getEnclosingElement();
            Name pkName = mElementUtils.getPackageOf(enclosingElement).getQualifiedName();

            //获取类名
            String className = enclosingElement.getSimpleName().toString();

            StringBuilder builder = new StringBuilder();
            builder.append("package ").append(pkName).append(";\n\n");
            builder.append("//Auto generated by apt,do not modify!!\n\n");
            builder.append("public class ").append(className).append("_ViewBinding").append(" { \n\n");
            builder.append("\tpublic ").append(className).append("_ViewBinding(").append(className).append(" activity){ \n");

            for (Element element : value) {
                VariableElement bindViewElement = (VariableElement) element;
                String bindViewFiledName = bindViewElement.getSimpleName().toString();
                String bindViewFiledClassType = bindViewElement.asType().toString();

                Bind bindView = element.getAnnotation(Bind.class);
                int id = bindView.value();
                note(String.format(Locale.getDefault(), "%s %s = %d", bindViewFiledClassType, bindViewFiledName, id));

                String info = String.format(Locale.getDefault(),
                        "\t\tactivity.%s = activity.findViewById(%d);\n", bindViewFiledName, id);
                builder.append(info);
            }

            builder.append("\t}\n");
            builder.append("}");

            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void createFile(TypeElement enclosingElement, String bindViewFiledClassType, String bindViewFiledName, int id) {
//        String pkName = mElementUtils.getPackageOf(enclosingElement).getQualifiedName().toString();
//        try {
//            JavaFileObject jfo = mFiler.createSourceFile(pkName + ".ViewBinding", new Element[]{});
//            Writer writer = jfo.openWriter();
//            writer.write(brewCode(pkName, bindViewFiledClassType, bindViewFiledName, id));
//            writer.flush();
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    private String brewCode(String pkName, String bindViewFiledClassType, String bindViewFiledName, int id) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("package " + pkName + ";\n\n");
//        builder.append("//Auto generated by apt,do not modify!!\n\n");
//        builder.append("public class ViewBinding { \n\n");
//        builder.append("public static void main(String[] args){ \n");
//        String info = String.format("%s %s = %d", bindViewFiledClassType, bindViewFiledName, id);
//        builder.append("System.out.println(\"" + info + "\");\n");
//        builder.append("}\n");
//        builder.append("}");
//        return builder.toString();
//    }

    private void note(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }
}