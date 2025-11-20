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
    val renderer = UIGraphicsImageRenderer(size = CGSizeMake(width = size, height = size))

    return renderer.imageWithActions { context ->
        UIColor.blackColor.setFill()
        val circlePath = UIBezierPath.bezierPathWithOvalInRect(
            CGRectMake(x = 0.0, y = 0.0, width = size, height = size)
        )
        circlePath.fill()

        val textAttributes: Map<Any?, *>? = mapOf(
            NSForegroundColorAttributeName to UIColor.whiteColor,
            NSFontAttributeName to UIFont.boldSystemFontOfSize(16.0)
        )

        val countText = count.toString() as NSString
        val textSize = countText.sizeWithAttributes(textAttributes)
        val textRect = CGRectMake(
            x = (size - textSize.useContents { width }) / 2.0,
            y = (size - textSize.useContents { height }) / 2.0,
            width = textSize.useContents { width },
            height = textSize.useContents { height }
        )
        countText.drawInRect(textRect, withAttributes = textAttributes)
    }
}