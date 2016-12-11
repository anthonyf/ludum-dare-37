(ns ld37.food-actor
  (:import (com.badlogic.gdx.scenes.scene2d.ui Image))
  (:require [ld37.asset-manager :as am]
            [ld37.common :as c]))

(defn make-food-actor!
  [game]
  (let [food (proxy [Image] [(am/make-texture-drawable "images/spider.png")]
               (act [delta]
                 (let [{[food-x food-x :as food-pos] :food} @game]
                   (c/set-actor-game-position this food-pos))))]
    food))
