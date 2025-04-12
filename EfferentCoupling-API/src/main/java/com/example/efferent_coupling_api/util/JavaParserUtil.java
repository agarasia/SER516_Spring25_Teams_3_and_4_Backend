package com.example.efferent_coupling_api.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class JavaParserUtil {

    public static Map<String, Integer> computeEfferentCoupling(File repoDir) {
        Map<String, Set<String>> couplingMap = new HashMap<>();
        scanJavaFiles(repoDir, couplingMap);

        Map<String, Integer> efferentCouplingMetrics = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : couplingMap.entrySet()) {
            efferentCouplingMetrics.put(entry.getKey(), entry.getValue().size());
        }

        return efferentCouplingMetrics;
    }

    private static void scanJavaFiles(File dir, Map<String, Set<String>> couplingMap) {
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                scanJavaFiles(file, couplingMap);
            } else if (file.getName().endsWith(".java")) {
                analyzeJavaFile(file, couplingMap);
            }
        }
    }

    private static void analyzeJavaFile(File javaFile, Map<String, Set<String>> couplingMap) {
        try {
            CompilationUnit cu = new JavaParser().parse(javaFile).getResult().orElse(null);
            if (cu == null) return;

            String packageName = cu.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("default");

            Optional<ClassOrInterfaceDeclaration> classDeclaration = cu.findFirst(ClassOrInterfaceDeclaration.class);
            if (classDeclaration.isEmpty()) return;
            
            String className = classDeclaration.get().getNameAsString();
            String fullClassName = packageName + "." + className;

            couplingMap.putIfAbsent(fullClassName, new HashSet<>());

            for (ImportDeclaration importDecl : cu.getImports()) {
                String importedClass = importDecl.getName().toString();
                if (!importedClass.startsWith("java.") && !importedClass.startsWith("javax.")) {
                    couplingMap.get(fullClassName).add(importedClass);
                }
            }

            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cls -> {
                cls.getExtendedTypes().forEach(extendedType -> {
                    String extendedClass = extendedType.getNameAsString();
                    couplingMap.get(fullClassName).add(packageName + "." + extendedClass);
                });

                cls.getImplementedTypes().forEach(implementedType -> {
                    String implementedClass = implementedType.getNameAsString();
                    couplingMap.get(fullClassName).add(packageName + "." + implementedClass);
                });

                cls.getAnnotations().forEach(annotation -> {
                    String annotationName = annotation.getNameAsString();
                    if (!annotationName.startsWith("java.") && !annotationName.startsWith("javax.")) {
                        couplingMap.get(fullClassName).add(annotationName);
                    }
                });
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
