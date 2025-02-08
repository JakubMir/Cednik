package com.example.cednik.ui.screens.add_edit_journey

import com.example.cednik.model.Journey

class AddEditJourneyScreenData {
    var journey: Journey = Journey ("")
    var journeyNameError: String? = null

    var textString: String = ""

    var locationError: String? = null

    var latitude: Double? = null
    var longitude: Double? = null

    var userLatitude: Double = 0.0
    var userLongitude: Double = 0.0

}