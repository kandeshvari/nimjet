package generated.psi.impl

import com.intellij.lang.ASTNode
import generated.psi.Expression
import generated.psi.InfixExpr

class InfixExprImpl(node: ASTNode) : BaseExpression(node), InfixExpr {

	override fun getExpression(): Expression? {
		return findChildByClass(Expression::class.java)
	}

}
