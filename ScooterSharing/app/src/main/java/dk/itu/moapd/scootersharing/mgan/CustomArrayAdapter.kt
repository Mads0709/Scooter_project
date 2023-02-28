package dk.itu.moapd.scootersharing.mgan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CustomArrayAdapter(
    context: Context, private var resource: Int,
    data: List<Scooter>) :
    ArrayAdapter<Scooter>(context, R.layout.list_rides, data) {
    private class ViewHolder(view: View) {
        val name: TextView = view.findViewById(R.id.list_item_name)
        val location: TextView = view.findViewById(R.id.list_item_location)
        val timestamp: TextView = view.findViewById(R.id.list_item_timestamp)
    }

    override fun getView(
        position: Int, convertView: View?,
        parent: ViewGroup
    ): View {
        var view = convertView
        val viewHolder: ViewHolder
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(resource, parent, false)
            viewHolder = ViewHolder(view)
        } else
            viewHolder = view.tag as ViewHolder
            view?.tag = viewHolder
            val scooter = getItem(position)
            viewHolder.name.text = scooter?.name.toString()
            viewHolder.location.text = scooter?.location.toString()
            viewHolder.timestamp.text = scooter?.timestamp.toString()
            return view!!
    }
}