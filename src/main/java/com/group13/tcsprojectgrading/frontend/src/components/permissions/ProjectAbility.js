import { defineAbility } from '@casl/ability';
import {Ability, AbilityBuilder} from "@casl/ability";
import { createCanBoundTo } from "@casl/react";

const ability = new Ability([])

function updateAbility(ability, privileges, user) {
  const { can, rules } = new AbilityBuilder();
  // console.log("update ability")
  privileges.forEach((privilege) => {
    let privileges1 = privilege.name.split("_")
    let action = privileges1[1]
    let object = privileges1[0]
    if (privileges1.length === 2) {
      can(action, object)
    } else {
      let type = privileges1[2]
      if (type === "assigned") {
        can(action, object, {id: user.id})
        // can(action, object)
      }
    }
  })

  ability.update(rules)
}

const Can = createCanBoundTo(ability)

export {Can, ability, updateAbility};
