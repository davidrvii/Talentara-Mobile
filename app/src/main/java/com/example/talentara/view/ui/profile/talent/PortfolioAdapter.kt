package com.example.talentara.view.ui.profile.talent

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.talentara.R
import com.example.talentara.data.model.response.portfolio.TalentPortfolioItem
import com.example.talentara.databinding.PortfolioItemBinding
import com.example.talentara.view.ui.portfolio.detail.PortfolioDetailActivity
import java.text.SimpleDateFormat
import java.util.Locale

class PortfolioAdapter(
    private var listPortfolio: List<TalentPortfolioItem>,
) : RecyclerView.Adapter<PortfolioAdapter.ViewHolder>() {

    class ViewHolder(private val binding: PortfolioItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TalentPortfolioItem) {

            //Get First Item of Product Type and Platform
            val firstProduct = item.productTypes?.split("|")?.firstOrNull() ?: "-"
            val firstPlatform = item.platforms?.split("|")?.firstOrNull() ?: "-"

            val formattedDeadline = formatDeadline(item.startDate.toString(),
                item.endDate.toString()
            )
            val deadline = formattedDeadline

            binding.tvProject.text = item.portfolioName.orEmpty()
            binding.tvClient.text = item.clientName.orEmpty()
            binding.tvProduct.text = itemView.context.getString(R.string.project_product, firstProduct, firstPlatform)
            binding.tvCompleted.text = deadline

            if (item.portfolioLabel == "Portfolio") {
                binding.ivPortfolioBadge.load(R.drawable.ic_portfolio_outside) {
                    placeholder(R.drawable.blank_avatar)
                    error(R.drawable.blank_avatar)
                }
            } else if (item.portfolioLabel == "Talentara"){
                binding.ivPortfolioBadge.load(R.drawable.ic_portfolio_inside) {
                    placeholder(R.drawable.blank_avatar)
                    error(R.drawable.blank_avatar)
                }
            }

            binding.root.setOnClickListener {
                val intent = Intent(itemView.context, PortfolioDetailActivity::class.java).apply {
                    putExtra(PortfolioDetailActivity.PORTFOLIO_ID, item.portfolioId)
                }
                itemView.context.startActivity(intent)
            }
        }

        private fun formatDeadline(start: String, end: String): String {
            // Input and output patterns
            val inputPattern = "yyyy-MM-dd"
            val outputPattern = "dd MMM yyyy"
            val locale = Locale.getDefault()

            return try {
                val sdfIn = SimpleDateFormat(inputPattern, Locale.US)
                val sdfOut = SimpleDateFormat(outputPattern, locale)

                val sDate = sdfIn.parse(start)
                val eDate = sdfIn.parse(end)

                if (sDate != null && eDate != null) {
                    "${sdfOut.format(sDate)} - ${sdfOut.format(eDate)}"
                } else {
                    // fallback to raw strings
                    "$start - $end"
                }
            } catch (e: Exception) {
                // If parsing fails, show raw
                e.printStackTrace()
                "$start - $end"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            PortfolioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listPortfolio[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = listPortfolio.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<TalentPortfolioItem>) {
        Log.d("PortfolioAdapter", "updateData: ${newList.size} item(s) loaded")
        listPortfolio = newList
        notifyDataSetChanged()
    }
}