(ns ld37.hairballs
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Group)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.actions Actions
                                                    MoveToAction
                                                    RotateToAction
                                                    ParallelAction))
  (:require [ld37.asset-manager :as am]
            [ld37.common :as c]
            [ld37.jukebox :as j]
            [clojure.set :as set]))

(defn make-hairball
  [[x y :as pos] head-pos]
  (let [actor (Image. (am/make-texture-drawable "images/hairball.png"))
        action (Actions/parallel (Actions/moveTo (* c/tile-width x)
                                                 (* c/tile-width y)
                                                 c/game-speed)
                                 (Actions/rotateTo (rand-int 360)))]
    (c/set-actor-game-position actor head-pos)
    (.setOrigin actor
                (/ (.getWidth actor) 2)
                (/ (.getHeight actor) 2))
    (.addAction actor action)
    actor))

(set/difference #{1 2 3} #{ 2 3})

(defn make-hairballs!
  [game]
  (let [prev-hairballs (atom #{})
        hairballs-actor (proxy [Group] []
                          (act [delta]
                            (let [{:keys [hairballs snake]} @game
                                  [head-pos & _] snake]
                              (when (not= @prev-hairballs hairballs)
                                (doseq [[x y :as hairball] (set/difference hairballs @prev-hairballs)]
                                  (let [hairball-actor (make-hairball hairball head-pos)]
                                    (.addActor this hairball-actor)
                                    hairball-actor))
                                (reset! prev-hairballs hairballs)
                                (j/play-sound :hairball))
                              (proxy-super act delta))))]
    hairballs-actor))
