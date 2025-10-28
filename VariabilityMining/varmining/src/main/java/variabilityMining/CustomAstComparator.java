package variabilityMining;

import java.util.List;
import java.util.stream.Collectors;

import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.MoveOperation;
import gumtree.spoon.diff.operations.Operation;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
/*
*Copyright (c) 2024 Johannes Kepler University Linz
*LIT Cyber-Physical Systems Lab
*Contributors:
*Alexander Stummer - initial API and implementation
*/
public class CustomAstComparator {
	
	public List<Operation> compare(CtElement original, CtElement revised) {
		
		AstComparator comparator = new AstComparator();
		
		Diff diff = comparator.compare(original, revised);
				
		return diff.getRootOperations().stream().filter(operation -> !(operation instanceof MoveOperation))
				.filter(o -> matchesType(o.getSrcNode())).toList();
	}
	
	private boolean matchesType(CtElement element) {
		return element instanceof CtAssignment || element instanceof CtInvocation 
				|| element instanceof CtNewClass || element instanceof CtIf || element instanceof CtSwitch 
				|| element instanceof CtFor || element instanceof CtForEach || element instanceof CtWhile
				|| element instanceof CtDo || element instanceof CtReturn || element instanceof CtThrow
				|| element instanceof CtLocalVariable || element instanceof CtClass 
				|| element instanceof CtMethod || element instanceof CtBlock || element instanceof CtSynchronized 
				|| element instanceof CtTry || element instanceof CtCatch || element instanceof CtField || element instanceof CtConstructor;
	}
}
