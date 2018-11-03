package it.unibo.dcs.service

import com.google.gson.GsonBuilder
import it.unibo.dcs.commons.Configurator
import it.unibo.dcs.commons.dataaccess

package object webapp {

  private[webapp] val gson = Configurator[GsonBuilder]
    .andThen(dataaccess.gsonConfiguration)
    .apply(new GsonBuilder())
    .create()

}
