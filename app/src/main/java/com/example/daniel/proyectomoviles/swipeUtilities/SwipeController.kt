package com.example.daniel.proyectomoviles.swipeUtilities

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.*
import android.view.MotionEvent
import android.view.View
import com.example.daniel.proyectomoviles.R


//Fuente: https://codeburst.io/android-swipe-menu-with-recyclerview-8f28a235ff28

enum class ButtonsState {
    GONE,
    LEFT_VISIBLE,
    RIGHT_VISIBLE
}

class SwipeController(requireContext: Context, buttonActions:SwipeControllerActions) : Callback() {

    var context = requireContext
    var buttonActions = buttonActions


    private var buttonShowedState = ButtonsState.GONE
    private var buttonWidth = 210f
    private var buttonInstance: RectF? = null

    private var currentItemViewHolder: RecyclerView.ViewHolder? = null

    private var swipeBack = false

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        return ItemTouchHelper.Callback.makeMovementFlags(0, LEFT or RIGHT)
    }

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {

        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {

    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {

        if(swipeBack){
            swipeBack = buttonShowedState != ButtonsState.GONE
            return 0
        }


        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onChildDraw(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

        var dX = dX
        if (actionState == ACTION_STATE_SWIPE) {
            if (buttonShowedState != ButtonsState.GONE) {
                if (buttonShowedState == ButtonsState.LEFT_VISIBLE) dX = Math.max(dX, buttonWidth)
                if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) dX = Math.min(dX, -buttonWidth)
               super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
            else {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        if (buttonShowedState == ButtonsState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        currentItemViewHolder = viewHolder
   }

    private fun drawButtons(c: Canvas?, viewHolder: RecyclerView.ViewHolder?) {
        val buttonWidthWithoutPadding = buttonWidth - 6
        val corners = 10f

        val itemView = viewHolder?.itemView
        val p = Paint()

        val leftButton = RectF(itemView!!.getLeft().toFloat() + 10, itemView.getTop().toFloat() + 10, itemView.left + buttonWidthWithoutPadding, itemView.getBottom().toFloat())


        p.color = Color.LTGRAY
        c?.drawRoundRect(leftButton, corners, corners, p)
        drawText(context.getString(R.string.boton_ver_mas), c, leftButton, p)

        val rightButton = RectF(itemView.right - buttonWidthWithoutPadding, itemView.top.toFloat() + 10, itemView.right.toFloat() - 20, itemView.bottom.toFloat())

        p.color = Color.BLACK
        c?.drawRoundRect(rightButton, corners, corners, p)
        drawText(context.getString(R.string.boton_emitir_pago), c, rightButton, p)

        buttonInstance = null
        if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
            buttonInstance = leftButton
        }else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
            buttonInstance = rightButton
        }

    }

    private fun drawText(text: String, c: Canvas?, button: RectF, p: Paint) {

        val textSize = 42f
        p.color = Color.WHITE
        p.isAntiAlias = true
        p.textSize = textSize
        p.isFakeBoldText = true

        val textWidth = p.measureText(text)
        c?.drawText(text, button.centerX() - textWidth / 2, button.centerY() + textSize / 2, p)

    }

    private fun setTouchListener(c: Canvas?,
                                 recyclerView: RecyclerView?,
                                 viewHolder: RecyclerView.ViewHolder?,
                                 dX: Float, dY: Float,
                                 actionState: Int, isCurrentlyActive: Boolean) {



        recyclerView?.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                swipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP

                if (swipeBack) {
                    if (dX < -buttonWidth) buttonShowedState = ButtonsState.RIGHT_VISIBLE
                    else if (dX > buttonWidth) buttonShowedState = ButtonsState.LEFT_VISIBLE

                    if (buttonShowedState != ButtonsState.GONE) {
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        setItemsClickable(recyclerView, false)
                    }

                }
                return false
            }


        })
    }

    private fun setItemsClickable(recyclerView: RecyclerView, isClickable: Boolean) {

        for (i in 0 until recyclerView.childCount) {
            recyclerView.getChildAt(i).isClickable = isClickable
        }


    }

    private fun setTouchDownListener(c: Canvas?, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
            false
        }

    }

    private fun setTouchUpListener(c: Canvas?, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                super.onChildDraw(c, recyclerView, viewHolder, 0f, dY, actionState, isCurrentlyActive)
                recyclerView.setOnTouchListener { v, event -> false }
                setItemsClickable(recyclerView, true)
                swipeBack = false

                //LISTENERS DE BOTONES 'MAS' Y 'PAGAR'
                if(buttonActions != null && buttonInstance != null && buttonInstance!!.contains(event.x, event.y)){

                    if(buttonShowedState == ButtonsState.LEFT_VISIBLE){
                        buttonActions.onLeftClicked(viewHolder!!.adapterPosition)
                    }else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE){
                        buttonActions.onRightClicked(viewHolder!!.adapterPosition)
                    }

                }

                buttonShowedState = ButtonsState.GONE
                currentItemViewHolder = null
            }
            false
        }
    }

    fun onDraw(c: Canvas) {
        if (currentItemViewHolder != null) {
            drawButtons(c, currentItemViewHolder)
        }
    }


}