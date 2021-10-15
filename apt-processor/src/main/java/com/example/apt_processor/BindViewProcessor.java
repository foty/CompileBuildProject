package com.example.apt_processor;

import com.example.annotation.FindView;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Process.class)
public class BindViewProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Messager messager; //用来打印message

    /**
     * 初始化函数，会被注解处理工具调用。一般通过ProcessingEnviroment获取其他有用的工具类，如
     * Elements, Types和Filer等等。
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
    }

    /**
     * 指定注解处理器是给哪个注解使用的(使用对象)。通常添加到set集合返回
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<>();
        set.add(FindView.class.getCanonicalName());
        return set;
    }

    /**
     * 指定java版本
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 生成的java代码模板
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "构建开始。。。。。"); // Diagnostic类比Log作用

        //第一步、得到当前的所有注解集合(使用了@FindView 标记),主要是通过遍历
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(FindView.class);
        messager.printMessage(Diagnostic.Kind.NOTE, "elements.size(): " + elements.size());

        //先将得到的注解保存
        HashMap<Integer, String> eMap = new HashMap<>();

        for (Element e : elements) {
            messager.printMessage(Diagnostic.Kind.NOTE, "e.getSimpleName(): " + e.getSimpleName());
            messager.printMessage(Diagnostic.Kind.NOTE, "e.toString(): " + e.toString());
            //取出注解的内容(就是对应的id)
            FindView a = e.getAnnotation(FindView.class);
            eMap.put(a.value(), e.getSimpleName().toString());

            messager.printMessage(Diagnostic.Kind.NOTE, "a.value()= " + a.value());
            // 对象类型
            String type = e.asType().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, "type= " + type);

            //2、生成java文件。有2种办法：字符串拼接与javapoet库自动生成

            //2-1、获取到包名: getQualifiedName()
            String packageName = elementUtils.getPackageOf(e).getQualifiedName().toString(); // 获取全路径。
            messager.printMessage(Diagnostic.Kind.NOTE, "packageName: " + packageName);
            //2-2、获取类名
            String className = e.getEnclosingElement().getSimpleName().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, "className: " + className);

            try {
                // 创建一个这样的文件，参数为类名
                JavaFileObject file = processingEnv.getFiler().createSourceFile(className + "_ViewFinding");
                // 开启写入流
                Writer writer = file.openWriter();
                //写入相关内容
                writer.write(" package " + packageName + ";\n\n");
                writer.write(" public class " + className + "_ViewFinding {" + "\n");
                writer.write("    public void find (" + packageName + "." + e.getEnclosingElement().getSimpleName()
                        + " root ) { \n");
                writer.write("        root." + e.getSimpleName().toString() + " = (" + type + ")" + "(((android.app.Activity) root)"
                        + ".findViewById(" + a.value() + "));\n");
                writer.write("    }\n");
                writer.write("\n");
                writer.write(" }");
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
//            getJavaClassDoc(eMap);
        }

        messager.printMessage(Diagnostic.Kind.NOTE, "构建结束");

        //3、完成生成，返回true
        return true;
    }

    private void getJavaClassDoc(HashMap<Integer, String> eMap) {

    }

}
