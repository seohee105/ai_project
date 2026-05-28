package com.yourteam.newdang

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout

class AppearanceActivity : AppCompatActivity() {

    private val vm: AppearanceViewModel by viewModels()

    private lateinit var silhouette: SilhouetteView
    private lateinit var colorGrid:  RecyclerView
    private lateinit var partTabs:   TabLayout
    private lateinit var matchBadge: View
    private lateinit var matchName:  TextView
    private lateinit var summaryTop: TextView
    private lateinit var summaryBottom: TextView
    private lateinit var summaryHair:   TextView

    private var selectedPart = "top"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appearance)

        bindViews()
        setupTabs()
        setupColorGrid()
        observeViewModel()

        findViewById<View>(R.id.confirmBtn).setOnClickListener {
            val profile = vm.profile
            // TODO: 다음 화면으로 전달
        }
    }

    private fun bindViews() {
        silhouette    = findViewById(R.id.silhouetteView)
        colorGrid     = findViewById(R.id.colorGrid)
        partTabs      = findViewById(R.id.partTabs)
        matchBadge    = findViewById(R.id.matchColorBadge)
        matchName     = findViewById(R.id.matchColorName)
        summaryTop    = findViewById(R.id.summaryTop)
        summaryBottom = findViewById(R.id.summaryBottom)
        summaryHair   = findViewById(R.id.summaryHair)
    }

    private fun setupTabs() {
        listOf("상의", "하의", "머리").forEach {
            partTabs.addTab(partTabs.newTab().setText(it))
        }
        partTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectedPart = when (tab.position) { 0 -> "top"; 1 -> "bottom"; else -> "hair" }
                refreshColorGrid()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupColorGrid() {
        colorGrid.layoutManager = GridLayoutManager(this, 6)
        refreshColorGrid()
    }

    private fun refreshColorGrid() {
        val current = when (selectedPart) {
            "top"    -> vm.topColor.value
            "bottom" -> vm.bottomColor.value
            else     -> vm.hairColor.value
        }
        colorGrid.adapter = ColorAdapter(ColorPalette.colors, current) { selected ->
            vm.setColor(selectedPart, selected)
            refreshColorGrid()
        }
    }

    private fun observeViewModel() {
        vm.topColor.observe(this) { color ->
            silhouette.applyProfile(vm.profile)
            summaryTop.text = color.nameKr
            matchBadge.background.setTint(Color.parseColor(color.hexColor))
            matchName.text = color.nameKr
        }
        vm.bottomColor.observe(this) { color ->
            silhouette.applyProfile(vm.profile)
            summaryBottom.text = color.nameKr
        }
        vm.hairColor.observe(this) { color ->
            silhouette.applyProfile(vm.profile)
            summaryHair.text = color.nameKr
        }
    }
}

class ColorAdapter(
    private val colors: List<ClothingColor>,
    private val selected: ClothingColor?,
    private val onSelect: (ClothingColor) -> Unit
) : RecyclerView.Adapter<ColorAdapter.VH>() {

    inner class VH(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_color_circle, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val color = colors[position]
        val circle = holder.view.findViewById<View>(R.id.colorCircle)
        circle.background.setTint(Color.parseColor(color.hexColor))
        circle.isSelected = (color.hexColor == selected?.hexColor)
        circle.setOnClickListener { onSelect(color) }
    }

    override fun getItemCount() = colors.size
}