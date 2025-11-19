package net.thechance.mena.faith.presentation.map

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSString
import platform.UIKit.NSFontAttributeName
import platform.UIKit.NSForegroundColorAttributeName
import platform.UIKit.UIBezierPath
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIImage
import platform.UIKit.drawInRect
import platform.UIKit.sizeWithAttributes

@OptIn(ExperimentalForeignApi::class)
fun createClusterIcon(count: Int): UIImage {
    val size = 44.0
    val renderer = UIGraphicsImageRenderer(size = CGSizeMake(size, size))

    return renderer.imageWithActions { context ->
        UIColor.blackColor.setFill()
        val circlePath = UIBezierPath.bezierPathWithOvalInRect(
            CGRectMake(0.0, 0.0, size, size)
        )
        circlePath.fill()

        val textAttributes: Map<Any?, *>? = mapOf(
            NSForegroundColorAttributeName to UIColor.whiteColor,
            NSFontAttributeName to UIFont.boldSystemFontOfSize(16.0)
        )

        val countText = count.toString() as NSString
        val textSize = countText.sizeWithAttributes(textAttributes)
        val textRect = CGRectMake(
            (size - textSize.useContents { width }) / 2.0,
            (size - textSize.useContents { height }) / 2.0,
            textSize.useContents { width },
            textSize.useContents { height }
        )
        countText.drawInRect(textRect, withAttributes = textAttributes)
    }
}