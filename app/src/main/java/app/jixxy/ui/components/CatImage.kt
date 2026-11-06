package app.jixxy.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.jixxy.character.R as CharR

/** Shows Jixxy's expression image from :character. Decorative (the screen text carries the meaning). */
@Composable
fun CatImage(pose: CatPose, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(drawableFor(pose)),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier.fillMaxWidth().heightIn(max = 180.dp),
    )
}

private fun drawableFor(pose: CatPose): Int = when (pose) {
    CatPose.PEEK -> CharR.drawable.jixxy_expr_peek
    CatPose.REALLY -> CharR.drawable.jixxy_expr_really
    CatPose.SMUG -> CharR.drawable.jixxy_expr_smug
    CatPose.NAP -> CharR.drawable.jixxy_expr_nap
    CatPose.OOH_NICE -> CharR.drawable.jixxy_expr_ooh_nice
    CatPose.SHOCKED -> CharR.drawable.jixxy_expr_shocked
}
