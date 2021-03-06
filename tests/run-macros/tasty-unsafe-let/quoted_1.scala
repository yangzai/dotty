import scala.quoted._

object Macros {

  inline def let[T](rhs: => T)(inline body: T => Unit): Unit =
    ${ impl('rhs, 'body) }

  private def impl[T: Type](rhs: Expr[T], body: Expr[T => Unit])(using Quotes) : Expr[Unit] = {
    import qctx.reflect._

    val rhsTerm = Term.of(rhs)

    import qctx.reflect._
    ValDef.let(Symbol.spliceOwner, rhsTerm) { rhsId =>
      Term.of(Expr.betaReduce('{$body(${rhsId.asExpr.asInstanceOf[Expr[T]]})})) // Dangerous uncheked cast!
    }.asExprOf[Unit]
  }


}
