package com.puff.include.asm.reflect;

import static com.puff.include.asm.Opcodes.ACC_PUBLIC;
import static com.puff.include.asm.Opcodes.ACC_SUPER;
import static com.puff.include.asm.Opcodes.ALOAD;
import static com.puff.include.asm.Opcodes.ARETURN;
import static com.puff.include.asm.Opcodes.ATHROW;
import static com.puff.include.asm.Opcodes.CHECKCAST;
import static com.puff.include.asm.Opcodes.DUP;
import static com.puff.include.asm.Opcodes.INVOKESPECIAL;
import static com.puff.include.asm.Opcodes.INVOKEVIRTUAL;
import static com.puff.include.asm.Opcodes.NEW;
import static com.puff.include.asm.Opcodes.POP;
import static com.puff.include.asm.Opcodes.RETURN;
import static com.puff.include.asm.Opcodes.V1_1;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import com.puff.include.asm.ClassWriter;
import com.puff.include.asm.MethodVisitor;

public abstract class ConstructorAccess<T> {
	boolean isNonStaticMemberClass;

	public boolean isNonStaticMemberClass() {
		return isNonStaticMemberClass;
	}

	/** Constructor for top-level classes and static nested classes.
	 * <p>
	 * If the underlying class is a inner (non-static nested) class, a new instance will be created using <code>null</code> as the
	 * this$0 synthetic reference. The instantiated object will work as long as it actually don't use any member variable or method
	 * fron the enclosing instance. */
	abstract public T newInstance();

	/** Constructor for inner classes (non-static nested classes).
	 * @param enclosingInstance The instance of the enclosing type to which this inner instance is related to (assigned to its
	 *           synthetic this$0 field). */
	abstract public T newInstance(Object enclosingInstance);

	@SuppressWarnings("unchecked")
	static public <T> ConstructorAccess<T> get(Class<T> type, boolean isInterface) {
		Class<?> enclosingType = type.getEnclosingClass();
		boolean isNonStaticMemberClass = enclosingType != null && type.isMemberClass() && !Modifier.isStatic(type.getModifiers());

		String className = type.getName();
		String accessClassName = className + "ConstructorAccess";
		if (accessClassName.startsWith("java."))
			accessClassName = "reflectasm." + accessClassName;
		Class<?> accessClass;

		AccessClassLoader loader = AccessClassLoader.get(type);
		synchronized (loader) {
			try {
				accessClass = loader.loadClass(accessClassName);
			} catch (ClassNotFoundException ignored) {
				String accessClassNameInternal = accessClassName.replace('.', '/');
				String classNameInternal = className.replace('.', '/');
				String enclosingClassNameInternal;
				Constructor<T> constructor = null;
				int modifiers = 0;
				if (!isNonStaticMemberClass) {
					enclosingClassNameInternal = null;
					try {
						constructor = type.getDeclaredConstructor((Class[]) null);
						modifiers = constructor.getModifiers();
					} catch (Exception ex) {
						throw new RuntimeException("Class cannot be created (missing no-arg constructor): " + type.getName(), ex);
					}
					if (Modifier.isPrivate(modifiers)) {
						throw new RuntimeException("Class cannot be created (the no-arg constructor is private): " + type.getName());
					}
				} else {
					enclosingClassNameInternal = enclosingType.getName().replace('.', '/');
					try {
						constructor = type.getDeclaredConstructor(enclosingType); // Inner
																					// classes
																					// should
																					// have
																					// this.
						modifiers = constructor.getModifiers();
					} catch (Exception ex) {
						throw new RuntimeException("Non-static member class cannot be created (missing enclosing class constructor): " + type.getName(), ex);
					}
					if (Modifier.isPrivate(modifiers)) {
						throw new RuntimeException("Non-static member class cannot be created (the enclosing class constructor is private): " + type.getName());
					}
				}
				String superclassNameInternal = Modifier.isPublic(modifiers) ? "com/puff/include/asm/reflect/PublicConstructorAccess"
						: "com/puff/include/asm/reflect/ConstructorAccess";

				ClassWriter cw = new ClassWriter(0);
				cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal, null, superclassNameInternal, null);

				insertConstructor(cw, superclassNameInternal, isInterface);
				insertNewInstance(cw, classNameInternal, isInterface);
				insertNewInstanceInner(cw, classNameInternal, enclosingClassNameInternal, isInterface);

				cw.visitEnd();
				accessClass = loader.defineClass(accessClassName, cw.toByteArray());
			}
		}
		ConstructorAccess<T> access;
		try {
			access = (ConstructorAccess<T>) accessClass.newInstance();
		} catch (Throwable t) {
			throw new RuntimeException("Exception constructing constructor access class: " + accessClassName, t);
		}
		if (!(access instanceof PublicConstructorAccess) && !AccessClassLoader.areInSameRuntimeClassLoader(type, accessClass)) {
			// Must test this after the try-catch block, whether the class has
			// been loaded as if has been defined.
			// Throw a Runtime exception here instead of an IllegalAccessError
			// when invoking newInstance()
			throw new RuntimeException((!isNonStaticMemberClass
					? "Class cannot be created (the no-arg constructor is protected or package-protected, and its ConstructorAccess could not be defined in the same class loader): "
					: "Non-static member class cannot be created (the enclosing class constructor is protected or package-protected, and its ConstructorAccess could not be defined in the same class loader): ")
					+ type.getName());
		}
		access.isNonStaticMemberClass = isNonStaticMemberClass;
		return access;
	}

	static private void insertConstructor(ClassWriter cw, String superclassNameInternal, boolean isInterface) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, superclassNameInternal, "<init>", "()V", isInterface);
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	static void insertNewInstance(ClassWriter cw, String classNameInternal, boolean isInterface) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "()Ljava/lang/Object;", null, null);
		mv.visitCode();
		mv.visitTypeInsn(NEW, classNameInternal);
		mv.visitInsn(DUP);
		// mv.visitMethodInsn(INVOKESPECIAL, classNameInternal, "<init>","()V");
		mv.visitMethodInsn(INVOKESPECIAL, classNameInternal, "<init>", "()V", isInterface);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(2, 1);
		mv.visitEnd();
	}

	static void insertNewInstanceInner(ClassWriter cw, String classNameInternal, String enclosingClassNameInternal, boolean isInterface) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
		mv.visitCode();
		if (enclosingClassNameInternal != null) {
			mv.visitTypeInsn(NEW, classNameInternal);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, enclosingClassNameInternal);
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", isInterface);
			mv.visitInsn(POP);
			mv.visitMethodInsn(INVOKESPECIAL, classNameInternal, "<init>", "(L" + enclosingClassNameInternal + ";)V", isInterface);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(4, 2);
		} else {
			mv.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
			mv.visitInsn(DUP);
			mv.visitLdcInsn("Not an inner class.");
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>", "(Ljava/lang/String;)V", isInterface);
			mv.visitInsn(ATHROW);
			mv.visitMaxs(3, 2);
		}
		mv.visitEnd();
	}
}
