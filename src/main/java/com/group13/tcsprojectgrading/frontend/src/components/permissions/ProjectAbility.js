import { defineAbility } from '@casl/ability';
import {Ability, AbilityBuilder} from "@casl/ability";
import { createCanBoundTo } from "@casl/react";

const ability = new Ability([])

function updateAbility(ability, privileges) {
  const { can, rules } = new AbilityBuilder();

  privileges.forEach((privilege) => {
    let action = privilege.name.split("_")[1]
    let object = privilege.name.split("_")[0]

    can(action, object)
  })

  ability.update(rules)
}

const Can = createCanBoundTo(ability)

export {Can, ability, updateAbility};
