package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtExpression

/**
 * Reports unreachable code.
 * Code can be unreachable because it is behind return, throw, continue or break expressions.
 * This unreachable code should be removed as it serves no purpose.
 *
 * <noncompliant>
 * for (i in 1..2) {
 *     break
 *     println() // unreachable
 * }
 *
 * throw IllegalArgumentException()
 * println() // unreachable
 *
 * fun f() {
 *     return
 *     println() // unreachable
 * }
 * </noncompliant>
 */
@RequiresTypeResolution
@ActiveByDefault(since = "1.0.0")
class UnreachableCode(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        "UnreachableCode",
        "Unreachable code detected. This code should be removed.",
    )

    override fun visitExpression(expression: KtExpression) {
        super.visitExpression(expression)
        if (bindingContext.diagnostics.forElement(expression).any { it.factory == Errors.UNREACHABLE_CODE }) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "This expression is unreachable code which should either be used or removed."
                )
            )
        }
    }
}
