package com.ltrudu.serverresponsetest.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;

import com.ltrudu.serverresponsetest.R;

public class OutlinedGroupView extends LinearLayout {
    
    private Paint outlinePaint;
    private Paint textPaint;
    private String hintText = "";
    private float cornerRadius = 12f;
    private float strokeWidth = 2f;
    private float textSize = 36f; // 12sp in pixels
    private float textPadding = 12f;
    private float topMargin = 24f;
    private int outlineColor;
    private int textColor;
    private RectF outlineRect = new RectF();
    private Path outlinePath = new Path();
    private boolean isInEditMode;
    
    public OutlinedGroupView(Context context) {
        super(context);
        init(context, null);
    }
    
    public OutlinedGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    public OutlinedGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    
    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        setOrientation(VERTICAL);
        isInEditMode = isInEditMode();
        
        // Convert dp to pixels
        float density = getResources().getDisplayMetrics().density;
        cornerRadius = 4f * density;
        strokeWidth = 1f * density;
        textSize = 12f * getResources().getDisplayMetrics().scaledDensity;
        textPadding = 8f * density;
        topMargin = 16f * density;
        
        // Get theme colors with fallbacks for preview mode
        if (isInEditMode) {
            // Fallback colors for preview mode
            outlineColor = Color.parseColor("#79747E"); // Material outline color
            textColor = Color.parseColor("#49454F"); // Material onSurfaceVariant color
        } else {
            try {
                TypedValue typedValue = new TypedValue();
                
                // Try to resolve outline color
                if (context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOutline, typedValue, true)) {
                    if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                        outlineColor = typedValue.data;
                    } else if (typedValue.resourceId != 0) {
                        outlineColor = context.getColor(typedValue.resourceId);
                    } else {
                        outlineColor = Color.parseColor("#79747E");
                    }
                } else {
                    outlineColor = Color.parseColor("#79747E");
                }
                
                // Try to resolve text color
                if (context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurfaceVariant, typedValue, true)) {
                    if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                        textColor = typedValue.data;
                    } else if (typedValue.resourceId != 0) {
                        textColor = context.getColor(typedValue.resourceId);
                    } else {
                        textColor = Color.parseColor("#49454F");
                    }
                } else {
                    textColor = Color.parseColor("#49454F");
                }
            } catch (Exception e) {
                // Fallback if theme resolution fails
                outlineColor = Color.parseColor("#79747E");
                textColor = Color.parseColor("#49454F");
            }
        }
        
        // Initialize paints
        outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outlinePaint.setColor(outlineColor);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(strokeWidth);
        
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        
        // Handle custom attributes
        if (attrs != null) {
            TypedArray a = null;
            try {
                a = context.obtainStyledAttributes(attrs, R.styleable.OutlinedGroupView);
                hintText = a.getString(R.styleable.OutlinedGroupView_hintText);
                if (hintText == null) hintText = isInEditMode ? "Preview Text" : "";
            } catch (Exception e) {
                hintText = isInEditMode ? "Preview Text" : "";
            } finally {
                if (a != null) {
                    a.recycle();
                }
            }
        }
        
        // Set padding to account for the outline and text
        setPadding((int) (strokeWidth + 4 * density), (int) (topMargin + strokeWidth), 
                  (int) (strokeWidth + 4 * density), (int) (strokeWidth + 4 * density));
    }
    
    public void setHintText(String hintText) {
        this.hintText = hintText != null ? hintText : "";
        invalidate();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (hintText == null || hintText.isEmpty()) return;
        
        int width = getWidth();
        int height = getHeight();
        
        if (width <= 0 || height <= 0) return;
        
        // Calculate text metrics
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float textWidth = textPaint.measureText(hintText);
        float textHeight = fontMetrics.descent - fontMetrics.ascent;
        
        // Calculate text position
        float textX = textPadding;
        float textY = topMargin / 2 - (fontMetrics.descent + fontMetrics.ascent) / 2;
        
        // Calculate outline bounds
        float left = strokeWidth / 2;
        float top = topMargin / 2;
        float right = width - strokeWidth / 2;
        float bottom = height - strokeWidth / 2;
        
        // Ensure we have valid bounds
        if (right <= left || bottom <= top) return;
        
        outlineRect.set(left, top, right, bottom);
        
        // Create path for outline with gap for text
        outlinePath.reset();
        
        try {
            // Ensure corner radius doesn't exceed half the height or width
            float actualCornerRadius = Math.min(cornerRadius, Math.min((right - left) / 4, (bottom - top) / 4));
            
            // Start from top-left corner
            outlinePath.moveTo(left, top + actualCornerRadius);
            if (actualCornerRadius > 0) {
                outlinePath.arcTo(left, top, left + actualCornerRadius * 2, top + actualCornerRadius * 2, 180, 90, false);
            }
            
            // Top line up to text start (with gap for text)
            float textStartGap = Math.max(textX - textPadding / 2, left + actualCornerRadius);
            outlinePath.lineTo(textStartGap, top);
            
            // Skip the text area
            float textEndGap = Math.min(textX + textWidth + textPadding / 2, right - actualCornerRadius);
            outlinePath.moveTo(textEndGap, top);
            
            // Continue top line to top-right corner
            outlinePath.lineTo(right - actualCornerRadius, top);
            if (actualCornerRadius > 0) {
                outlinePath.arcTo(right - actualCornerRadius * 2, top, right, top + actualCornerRadius * 2, 270, 90, false);
            }
            
            // Right line
            outlinePath.lineTo(right, bottom - actualCornerRadius);
            if (actualCornerRadius > 0) {
                outlinePath.arcTo(right - actualCornerRadius * 2, bottom - actualCornerRadius * 2, right, bottom, 0, 90, false);
            }
            
            // Bottom line
            outlinePath.lineTo(left + actualCornerRadius, bottom);
            if (actualCornerRadius > 0) {
                outlinePath.arcTo(left, bottom - actualCornerRadius * 2, left + actualCornerRadius * 2, bottom, 90, 90, false);
            }
            
            // Left line back to start
            outlinePath.lineTo(left, top + actualCornerRadius);
            
            // Draw the outline
            canvas.drawPath(outlinePath, outlinePaint);
            
            // Draw the text
            canvas.drawText(hintText, textX, textY, textPaint);
            
        } catch (Exception e) {
            // Fallback: draw simple rectangle if path creation fails
            canvas.drawRect(left, top, right, bottom, outlinePaint);
            canvas.drawText(hintText, textX, textY, textPaint);
        }
    }
}