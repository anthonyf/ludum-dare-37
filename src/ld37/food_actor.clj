(ns ld37.food-actor
  (:import (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.actions Actions
                                                    RepeatAction
                                                    TemporalAction))
  (:require [ld37.asset-manager :as am]
            [ld37.common :as c]
            [ld37.jukebox :as j]))

(defn make-food-actor!
  [game]
  (let [last-food-pos (atom nil)
        up (am/make-texture-drawable "images/spider-up.png")
        down (am/make-texture-drawable "images/spider-down.png")
        food (proxy [Image] [up]
               (act [delta]
                 (let [{[food-x food-x :as food-pos] :food} @game]
                   (when (and (not (nil? @last-food-pos))
                              (not= @last-food-pos food-pos))
                     (j/play-sound :eat))
                   (reset! last-food-pos food-pos)
                   (c/set-actor-game-position-centered this food-pos))
                 (proxy-super act delta)))]
    (.addAction food (Actions/repeat RepeatAction/FOREVER
                                     (proxy [TemporalAction] [1]
                                       (end []
                                         (.setDrawable food (if (= up (.getDrawable food))
                                                              down
                                                              up))
                                         (proxy-super end))
                                       (update [percent]
                                         ))))

    food))
