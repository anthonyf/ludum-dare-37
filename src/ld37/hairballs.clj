(ns ld37.hairballs
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Group)
           (com.badlogic.gdx.scenes.scene2d.ui Image))
  (:require [ld37.asset-manager :as am]
            [ld37.common :as c]))

(defn make-hairball
  []
  (Image. (am/make-texture-drawable "images/hairball.png")))

(defn make-hairballs!
  [game]
  (let [{:keys [hairballs]} @game
        hairball-actors (atom [])
        hairballs-actor (proxy [Group] []
                          (act [delta]
                            (let [{:keys [hairballs]} @game]
                              (when (not= (count @hairball-actors)
                                          (count hairballs))
                                (doseq [hairball-actor @hairball-actors]
                                  (.remove hairball-actor))
                                (reset! hairball-actors
                                        (map (fn [hairball]
                                               (let [hairball-actor (make-hairball)]
                                                 (c/set-actor-game-position hairball-actor hairball )
                                                 (.addActor this hairball-actor)
                                                 hairball-actor))
                                             hairballs)))
                              (proxy-super act delta))))]
    hairballs-actor))
