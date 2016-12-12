(ns ld37.food-actor
  (:import (com.badlogic.gdx.scenes.scene2d.ui Image))
  (:require [ld37.asset-manager :as am]
            [ld37.common :as c]
            [ld37.jukebox :as j]))

(defn make-food-actor!
  [game]
  (let [last-food-pos (atom nil)
        food (proxy [Image] [(am/make-texture-drawable "images/spider.png")]
               (act [delta]
                 (let [{[food-x food-x :as food-pos] :food} @game]
                   (when (and (not (nil? @last-food-pos))
                              (not= @last-food-pos food-pos))
                     (j/play-sound :eat))
                   (reset! last-food-pos food-pos)
                   (c/set-actor-game-position-centered this food-pos))
                 (proxy-super act delta)))]
    food))
